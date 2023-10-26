<#setting number_format="computer">

<body>
<h1>Sorry...</h1>

<p>Dear ${customer.firstName}, ${customer.lastName}, <br/><br/>
    your order #${order.id} is <b>NOT paid successfully</b>!
</p>

<p>
    The payment failure message is: ${paymentStatus.errorMessage}
</p>

<h3>Order details</h3>

<p>Total amount: ${charge.getAmount()} USD</p>

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

<hr/>

</body>