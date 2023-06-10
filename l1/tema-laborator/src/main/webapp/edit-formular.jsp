<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Formular student de actualizare</title>
        <meta charset="UTF-8">
    </head>
    <body>
        <h3>Formular student de actualizare</h3>

        <jsp:useBean id="studentBean" class="beans.StudentBean" />
        <jsp:setProperty name="studentBean" property="nume" value='<%=request.getAttribute("nume") %>'/>
        <jsp:setProperty name="studentBean" property="prenume" value='<%=request.getAttribute("prenume") %>'/>
        <jsp:setProperty name="studentBean" property="varsta" value='<%=request.getAttribute("varsta") %>'/>

        Actualizati datele despre student:
        <form action="./edit-student" method=post>
            Nume: <input type="text" name="nume" value=<jsp:getProperty name="studentBean" property="nume"/> />
            <br />
            Prenume: <input type="text" name="prenume" value=<jsp:getProperty name="studentBean" property="prenume"/> />
            <br />
            Varsta: <input type="number" name="varsta" value=<jsp:getProperty name="studentBean" property="varsta"/> />
            <br />
            <br />
            <button type="submit" name="submit">Trimite</button>
        </form>
        <form action="./index.jsp">
            <button type="submit" name="submit">Inapoi la prima pagina</button>
        </form>
    </body>
</html>