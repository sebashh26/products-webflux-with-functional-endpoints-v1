package inicio;

import model.Producto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
import service.ProductosService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class ApplicationTests {

    @Autowired
    ProductosService productosService;

    @Test
    @Order(1)
    void testProductCategory() {

        StepVerifier.create(productosService.productosCategoria("Alimentación"))
                .expectNextMatches(producto -> producto.getNombre().equals("azucar"))
                .expectNextMatches(producto -> producto.getNombre().equals("Leche"))
                .expectNextMatches(producto -> producto.getNombre().equals("Huevos"))
                .verifyComplete();


    }

    @Test
    @Order(2)
    void testDeleteProduct() {

        StepVerifier.create(productosService.eliminarProducto(108))
                .expectNextMatches(producto -> producto.getNombre().equals("pann"))
                .verifyComplete();

    }

    @Test
    @Order(3)
    void testCreateProduct() {

        Producto producto = new Producto(108, "pann", "Desayuno", 0.15, 50, true );
        StepVerifier.create(productosService.altaProducto(producto))
                .expectComplete()
                .verify();
    }

    @Test
    @Order(4)
    void testGetAllProducts() {
        StepVerifier.create(productosService.catalogo())
                .expectNextCount(8)
                .verifyComplete();
    }

}
