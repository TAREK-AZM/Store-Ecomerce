<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes"/>

    <!-- Variable to store total value -->
    <xsl:variable name="total-value">
        <xsl:value-of select="sum(//product/price)"/>
    </xsl:variable>

    <xsl:template match="/">
        <html>
            <head>
                <title>Monthly Store Report</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    table { border-collapse: collapse; width: 100%; margin-top: 20px; }
                    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    th { background-color: #f2f2f2; }
                    .summary { margin: 20px 0; padding: 15px; background-color: #f8f9fa; }
                    .low-stock { background-color: #ffe6e6; }
                </style>
            </head>
            <body>
                <h1>Monthly Store Report</h1>
                <div class="summary">
                    <h2>Summary</h2>
                    <p>Total Products: <xsl:value-of select="count(//product)"/></p>
                    <p>Total Items in Stock: <xsl:value-of select="sum(//product/stockQuantity)"/></p>
                    <p>Low Stock Items (Quantity ≤ 10): <xsl:value-of select="count(//product[stockQuantity &lt;= 10])"/></p>
                </div>

                <h2>Product Inventory</h2>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Category</th>
                        <th>Price ($)</th>
                        <th>Stock</th>
                    </tr>
                    <xsl:apply-templates select="//product">
                        <xsl:sort select="category"/>
                    </xsl:apply-templates>
                </table>

                <h2>Category Summary</h2>
                <table>
                    <tr>
                        <th>Category</th>
                        <th>Number of Products</th>
                        <th>Total Stock</th>
                    </tr>
                    <xsl:for-each select="//product[not(category=preceding::product/category)]/category">
                        <xsl:variable name="current-category" select="."/>
                        <tr>
                            <td><xsl:value-of select="$current-category"/></td>
                            <td><xsl:value-of select="count(//product[category=$current-category])"/></td>
                            <td><xsl:value-of select="sum(//product[category=$current-category]/stockQuantity)"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="product">
        <tr>
            <xsl:if test="stockQuantity &lt;= 10">
                <xsl:attribute name="class">low-stock</xsl:attribute>
            </xsl:if>
            <td><xsl:value-of select="id"/></td>
            <td><xsl:value-of select="title"/></td>
            <td><xsl:value-of select="category"/></td>
            <td><xsl:value-of select="format-number(price, '#,##0.00')"/></td>
            <td><xsl:value-of select="stockQuantity"/></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>