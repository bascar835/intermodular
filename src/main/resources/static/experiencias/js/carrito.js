document.addEventListener("DOMContentLoaded", function () {
    mostrarCarrito();
});

// AÑADIR AL CARRITO (PARA SUGERENCIAS)
function addToCart(nombre, precio) {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];

    carrito.push({
        nombre: nombre,
        precio: parseFloat(precio)
    });

    localStorage.setItem("carrito", JSON.stringify(carrito));
    mostrarCarrito();
}

// MOSTRAR EL CARRITO
function mostrarCarrito() {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];

    const emptyCart = document.getElementById("emptyCart");
    const cartProducts = document.getElementById("cartProducts");
    const productsList = document.getElementById("productsList");
    const totalCarrito = document.getElementById("totalCarrito");

    if (!productsList || !emptyCart || !cartProducts || !totalCarrito) return;

    productsList.innerHTML = "";

    if (carrito.length === 0) {
        emptyCart.style.display = "block";
        cartProducts.style.display = "none";
        return;
    }

    emptyCart.style.display = "none";
    cartProducts.style.display = "block";

    let total = 0;

    carrito.forEach((producto, index) => {
        total += parseFloat(producto.precio);

        productsList.innerHTML += `
            <div class="cart-product">
                <h3>${producto.nombre}</h3>
                <p>Precio: ${producto.precio}€</p>
                <button class="btn-delete" onclick="eliminarProducto(${index})">Eliminar</button>
            </div>
            <hr>
        `;
    });

    totalCarrito.innerText = `Total: ${total.toFixed(2)}€`;

    productsList.innerHTML += `
        <button class="btn-secondary" onclick="vaciarCarrito()">Vaciar carrito</button>
    `;
}

// SE ELIMINARA EL PRODUCTO
function eliminarProducto(index) {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];

    carrito.splice(index, 1);

    localStorage.setItem("carrito", JSON.stringify(carrito));
    mostrarCarrito();
}

// ESTO HARA QUE QUEDE VACIO EL  CARRITO
function vaciarCarrito() {
    localStorage.removeItem("carrito");
    mostrarCarrito();
}
