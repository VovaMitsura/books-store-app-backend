<#setting number_format="computer">

<body>
<h1>Congratulations!</h1>

Dear ${customer.firstName}, ${customer.lastName} <br/><br/>
your order #${order.id} is <b>paid successfully</b>!

<h3>Order details</h3>

<p>Total amount: ${charge.getAmount()} USD</p>
<p>Shipped on: ${order.date}</p>

<h4>Items</h4>

<table width="100%" border="1px">
    <thead>
    <tr>
        <th>Product</th>
        <th>Qty</th>
        <th>Subtotal</th>
    </tr>
    </thead>

    <#list order.orderDetails as x >
        <tr>
            <td>${x.book.title}</td>
            <td>${x.quantity}</td>
            <td>${x.getTotalPrice()} USD</td>
        </tr>
    </#list>


    <tr>
        <td><strong>TOTAL</strong></td>
        <td>&nbsp;</td>
        <td><strong>${charge.getAmount()} USD</strong></td>
    </tr>

</table>

<h3>Payment details</h3>

<p>Transaction id: ${paymentStatus.id}</p>

<#assign card = paymentStatus.charge.paymentMethodDetails.card>
<p>Card: ${card.brand?capitalize} **** **** **** ${card.last4}, ${card.expMonth} / ${card.expYear}</p>

<a href="${paymentStatus.charge.receiptUrl}" type="button">Load receipt</a>


<hr/>
</body>