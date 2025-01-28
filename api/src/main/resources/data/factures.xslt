<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes"/>

    <xsl:template match="/invoice">
        <html>
            <head>
                <title>Invoice</title>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .invoice-header { background-color: #007BFF; color: white; padding: 20px; text-align: center; }
                    .invoice-details { margin: 20px; }
                    .invoice-table { width: 100%; border-collapse: collapse; }
                    .invoice-table th, .invoice-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    .invoice-table th { background-color: #f2f2f2; }
                    .total-amount { text-align: right; font-weight: bold; margin-top: 20px; }
                    .footer { text-align: center; margin-top: 40px; font-style: italic; }
                </style>
            </head>
            <body>
                <div class="invoice-header">
                    <h1>INVOICE</h1>
                </div>
                <div class="invoice-details">
                    <p><strong>Invoice #:</strong> <xsl:value-of select="id"/></p>
                    <p><strong>Date:</strong> <xsl:value-of select="date"/></p>
                    <p><strong>Command ID:</strong> <xsl:value-of select="commandId"/></p>
                    <p><strong>User ID:</strong> <xsl:value-of select="userId"/></p>
                </div>
                <table class="invoice-table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Quantity</th>
                            <th>Unit Price</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:for-each select="lines/line">
                            <tr>
                                <td><xsl:value-of select="product"/></td>
                                <td><xsl:value-of select="quantity"/></td>
                                <td>$<xsl:value-of select="unitPrice"/></td>
                                <td>$<xsl:value-of select="total"/></td>
                            </tr>
                        </xsl:for-each>
                    </tbody>
                </table>
                <div class="total-amount">
                    <p>Total Amount: $<xsl:value-of select="totalAmount"/></p>
                </div>
                <div class="footer">
                    <p>Thank you for your business!</p>
                    <p>Contact us at support@store.com</p>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
