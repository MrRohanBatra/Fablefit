class Cart {
    constructor(userId, items = [], totalPrice = 0) {
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    static fromJson(data) {
        return new Cart(
            data.userId,
            data.items || [],
            data.totalPrice || 0
        );
    }

 
    addProduct(product, size, color, quantity = 1) {
        const existingItem = this.items.find(
            (item) =>
                item.product._id === product._id &&
                item.size === size &&
                item.color === color
        );

        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            this.items.push({ product, size, color, quantity });
        }

        this.updateTotalPrice();
    }

    updateTotalPrice() {
        this.totalPrice = this.items.reduce(
            (sum, item) => sum + item.product.price * item.quantity,
            0
        );
    }

    removeProduct(productId, size, color) {
        this.items = this.items.filter(
            (item) =>
                !(
                    item.product._id === productId &&
                    item.size === size &&
                    item.color === color
                )
        );

        this.updateTotalPrice();
    }

    updateQuantity(productId, size, color, quantity) {
        const item = this.items.find(
            (i) =>
                i.product._id === productId &&
                i.size === size &&
                i.color === color
        );

        if (item) {
            item.quantity = quantity;
            this.updateTotalPrice();
        }
    }
    toJSON() {
        return {
            userId: this.userId,
            totalPrice: this.totalPrice,
            items: this.items.map((item) => ({
                product: item.product._id, // only send the ID
                size: item.size,
                color: item.color,
                quantity: item.quantity
            }))
        };
    }
    length() {
        return this.items.length;
    }
}
export default Cart
export {
    Cart,
}
