SELECT p.product_id, p.name, NVL(o.total_sold, 0) AS total_sold FROM PRODUCT p
LEFT OUTER JOIN (
    SELECT product_id, SUM(quantity) AS total_sold FROM ORDERS
    GROUP BY product_id HAVING SUM(quantity) < 10
) o ON o.product_id = p.product_id
WHERE p.AVAILABLE_FROM < add_months(SYSDATE,-1);