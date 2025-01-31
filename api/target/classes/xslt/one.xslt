<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Store Unified Report</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }
                    h1, h2 { color: #0056b3; text-align: center; }
                    table { border-collapse: collapse; width: 100%; margin-top: 20px; }
                    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    th { background-color: #0056b3; color: white; }
                    .section { margin: 30px 0; padding: 20px; background: #e9f7ff; border-radius: 8px; border: 1px solid #cfe2f3; }
                </style>
            </head>
            <body>
                <h1>📊 Store Unified Report</h1>

                <!-- Inventory Section -->
                <div class="section">
                    <h2>📦 Inventory Report</h2>
                    <p><strong>Total Products: </strong><xsl:value-of select="totalProducts"/></p>
                    <p><strong>Total Stock: </strong><xsl:value-of select="totalStock"/></p>
                    <h3>Low Stock Products</h3>
                    <table>
                        <tr><th>Product ID</th><th>Title</th><th>Stock Quantity</th></tr>
                        <xsl:for-each select="lowStockProducts/product">
                            <tr>
                                <td><xsl:value-of select="id"/></td>
                                <td><xsl:value-of select="title"/></td>
                                <td><xsl:value-of select="stockQuantity"/></td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </div>

                <!-- Sales Section -->
                <div class="section">
                    <h2>💰 Sales Report</h2>
                    <p><strong>Total Revenue: </strong>$<xsl:value-of select="format-number(totalRevenue, '#,##0.00')"/></p>
                    <h3>Best-Selling Products</h3>
                    <table>
                        <tr><th>Product ID</th><th>Sales Count</th></tr>
                        <xsl:for-each select="bestSellingProducts/entry">
                            <tr>
                                <td><xsl:value-of select="key"/></td>
                                <td><xsl:value-of select="value"/></td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </div>

                <!-- Category Section -->
                <div class="section">
                    <h2>📂 Category Report</h2>
                    <h3>Products by Category</h3>
                    <table>
                        <tr><th>Category</th><th>Product Count</th></tr>
                        <xsl:for-each select="productsByCategory/entry">
                            <tr>
                                <td><xsl:value-of select="key"/></td>
                                <td><xsl:value-of select="value"/></td>
                            </tr>
                        </xsl:for-each>
                    </table>
                    <h3>Revenue by Category</h3>
                    <table>
                        <tr><th>Category</th><th>Total Revenue ($)</th></tr>
                        <xsl:for-each select="revenueByCategory/entry">
                            <tr>
                                <td><xsl:value-of select="key"/></td>
                                <td><xsl:value-of select="format-number(value, '#,##0.00')"/></td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </div>

            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>