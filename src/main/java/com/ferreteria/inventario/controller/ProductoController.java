/**
 * Nombre del artefacto: ProductoController.java
 * Descripción: Controlador REST para las operaciones CRUD del catálogo de productos con control de acceso RBAC y OpenAPI sin dependencia de Lombok.
 * Fecha de creación: 2026-10-13
 * Última actualización: 2026-10-14
 * Autor: Equipo de Arquitectura - Ferretería
 */
package com.ferreteria.inventario.controller;

import com.ferreteria.inventario.dto.ApiResponseDto;
import com.ferreteria.inventario.dto.ErrorResponseDto;
import com.ferreteria.inventario.dto.ProductoRequestDto;
import com.ferreteria.inventario.dto.ProductoResponseDto;
import com.ferreteria.inventario.model.enums.EstadoProducto;
import com.ferreteria.inventario.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Endpoints para la gestión integral del inventario de productos de ferretería")
@SecurityRequirement(name = "BearerAuthentication")
public class ProductoController {

    private final ProductoService productoService;
    private final MessageSource messageSource;

    @Autowired
    public ProductoController(ProductoService productoService, MessageSource messageSource) {
        this.productoService = productoService;
        this.messageSource = messageSource;
    }

    @Operation(
            summary = "Crear nuevo producto",
            description = "Registra un nuevo producto en el catálogo. Roles autorizados: ROL_ADMIN, ROL_BODEGA."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o nombre duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_BODEGA')")
    public ResponseEntity<ApiResponseDto<ProductoResponseDto>> createProducto(
            @Valid @RequestBody ProductoRequestDto requestDto) {

        ProductoResponseDto created = productoService.createProducto(requestDto);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("producto.created.success", null, "Producto creado exitosamente", locale);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(message, created));
    }

    @Operation(
            summary = "Obtener producto por ID",
            description = "Consulta la información detallada de un producto por su identificador único. Roles autorizados: ROL_ADMIN, ROL_BODEGA, ROL_VENTAS."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_BODEGA') or hasAuthority('ROL_VENTAS')")
    public ResponseEntity<ApiResponseDto<ProductoResponseDto>> getProductoById(
            @Parameter(description = "Identificador único del producto", example = "1", required = true)
            @PathVariable Long id) {

        ProductoResponseDto producto = productoService.getProductoById(id);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("producto.found.success", null, "Producto obtenido exitosamente", locale);

        return ResponseEntity.ok(ApiResponseDto.success(message, producto));
    }

    @Operation(
            summary = "Listar catálogo de productos",
            description = "Obtiene el listado de productos con opciones de filtro por categoría y estado. Roles autorizados: ROL_ADMIN, ROL_BODEGA, ROL_VENTAS."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de productos retornado"),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_BODEGA') or hasAuthority('ROL_VENTAS')")
    public ResponseEntity<ApiResponseDto<List<ProductoResponseDto>>> getAllProductos(
            @Parameter(description = "Filtrar por categoría (ej. 'Herramientas Manuales')", required = false)
            @RequestParam(required = false) String categoria,
            @Parameter(description = "Filtrar por estado (DISPONIBLE, RESERVADA, DANADA)", required = false)
            @RequestParam(required = false) EstadoProducto estado) {

        List<ProductoResponseDto> productos = productoService.getAllProductos(categoria, estado);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("producto.list.success", null, "Listado de productos obtenido exitosamente", locale);

        return ResponseEntity.ok(ApiResponseDto.success(message, productos));
    }

    @Operation(
            summary = "Actualizar producto existente",
            description = "Actualiza los atributos de un producto registrado previamente. Roles autorizados: ROL_ADMIN, ROL_BODEGA."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_BODEGA')")
    public ResponseEntity<ApiResponseDto<ProductoResponseDto>> updateProducto(
            @Parameter(description = "Identificador único del producto a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDto requestDto) {

        ProductoResponseDto updated = productoService.updateProducto(id, requestDto);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("producto.updated.success", null, "Producto actualizado exitosamente", locale);

        return ResponseEntity.ok(ApiResponseDto.success(message, updated));
    }

    @Operation(
            summary = "Eliminar producto",
            description = "Elimina permanentemente un producto del catálogo de inventario. Rol autorizado exclusivamente: ROL_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes (requiere ROL_ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> deleteProducto(
            @Parameter(description = "Identificador único del producto a eliminar", example = "1", required = true)
            @PathVariable Long id) {

        productoService.deleteProducto(id);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("producto.deleted.success", new Object[]{id}, "Producto eliminado exitosamente", locale);

        return ResponseEntity.ok(ApiResponseDto.success(message));
    }
}
