package controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import model.Producto;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import service.ProductosService;

//@CrossOrigin("*")//use only in controller clasic like @RestController annotation, here not use
//@RestController
@Configuration
@RequiredArgsConstructor
public class ProductosController {

	private final ProductosService productosService;

//	@GetMapping(value="productos")
//	public ResponseEntity<Flux<Producto>> productos(){
//		return new ResponseEntity<>(productosService.catalogo(),HttpStatus.OK);
//	}
//	@GetMapping(value="productos/{categoria}")
//	public ResponseEntity<Flux<Producto>> productosCategoria(@PathVariable("categoria") String categoria){
//		return new ResponseEntity<>(productosService.productosCategoria(categoria),HttpStatus.OK);
//	}
//
//	@GetMapping(value="producto")
//	public ResponseEntity<Mono<Producto>> productoCodigo(@RequestParam("cod") int cod) {
//		return new ResponseEntity<>(productosService.productoCodigo(cod),HttpStatus.OK);
//	}
//	@PostMapping(value="alta",consumes=MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Mono<Void>> altaProducto(@RequestBody Producto producto) {
//		producto.setNuevoProducto(true);
//		return new ResponseEntity<>(productosService.altaProducto(producto),HttpStatus.OK);
//	}
//	@DeleteMapping(value="eliminar")
//	public Mono<ResponseEntity<Producto>> eliminarProducto(@RequestParam("cod") int cod) {
//		return productosService.eliminarProducto(cod)//Mono<Producto>
//				.map(p->new ResponseEntity<>(p,HttpStatus.OK))//Mono<ResponseEntity<Producto>>
//				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));//Mono<ResponseEntity<Producto>>
//	}
//	@PutMapping(value="actualizar")
//	public Mono<ResponseEntity<Producto>> actualizarProducto(@RequestParam("cod") int cod,@RequestParam("precio") double precio) {
//		return productosService.actualizarPrecio(cod, precio)//Mono<Producto>
//				.map(p->new ResponseEntity<>(p,HttpStatus.OK))//Mono<ResponseEntity<Producto>>
//				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));//Mono<ResponseEntity<Producto>>
//	}

	@Bean
	public RouterFunction<ServerResponse> responseRouterFunction() {

		return RouterFunctions.route(RequestPredicates.GET("productos"),
						request -> ServerResponse.ok()//bodybuilder
								.contentType(MediaType.TEXT_EVENT_STREAM)
								.body(productosService.catalogo(), Producto.class)//Mono<ServerResponse>
				)//RouterFunction<SeverResponse>
				.andRoute(RequestPredicates.GET("productos/{categoria}"),
						request -> ServerResponse.ok()
								.body(productosService.productosCategoria(request.pathVariable("categoria")), Producto.class))
				.andRoute(RequestPredicates.GET("producto"),
						request -> ServerResponse.ok()
								.contentType(MediaType.TEXT_EVENT_STREAM)
								.body(productosService.productoCodigo(request.queryParam("cod").map(Integer::parseInt).get()), Producto.class))
				.andRoute(RequestPredicates.POST("alta"),
						request -> request.bodyToMono(Producto.class)//Mono<Producto>
								.flatMap(producto -> {
									producto.setNuevoProducto(true);
									return productosService.altaProducto(producto);//Mono<Void>
								}).flatMap(v -> ServerResponse.ok()//bodybuilder
										.build()//Monoz<ServerResponse>
								)//Mono<ServerResponse>

				)
				.andRoute(RequestPredicates.DELETE("eliminar"),
						request -> productosService.eliminarProducto(request.queryParam("cod").map(s -> Integer.parseInt(s)).get())//Mono<Producto>
								.flatMap(producto -> ServerResponse.ok()//bodybuilder
										.bodyValue(producto)//Mono<ServerResponse>
								).switchIfEmpty(ServerResponse.notFound().build())//Mono<ServerResponse>
				)
				.andRoute(RequestPredicates.PUT("actualizar"),
						request -> productosService.actualizarPrecio(request.queryParam("cod").map(s -> Integer.parseInt(s)).get(),
										request.queryParam("precio").map(s -> Double.parseDouble(s)).get())//Mono<Producto>
								.flatMap(producto -> ServerResponse.ok()
										.bodyValue(producto))//Mono<ServerResponse>
								.switchIfEmpty(ServerResponse.notFound().build())//Mono<ServerResponse>
				);//RouterFunction<SeverResponse>

	}

	@Bean
    CorsWebFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(source);
	}
}
