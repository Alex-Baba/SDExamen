<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Formular student de actualizare</title>
        <meta charset="UTF-8">
    </head>
    <body>
        <h3>Formular student de actualizare</h3>

        <jsp:useBean id="studentBean" class="beans.StudentBean" />
        <jsp:setProperty name="studentBean" property="id" value='<%=request.getAttribute("id") %>'/>
        <jsp:setProperty name="studentBean" property="nume" value='<%=request.getAttribute("nume") %>'/>
        <jsp:setProperty name="studentBean" property="prenume" value='<%=request.getAttribute("prenume") %>'/>
        <jsp:setProperty name="studentBean" property="varsta" value='<%=request.getAttribute("varsta") %>'/>
        <jsp:setProperty name="studentBean" property="grupa" value='<%=request.getAttribute("grupa") %>'/>
        <jsp:setProperty name="studentBean" property="medie" value='<%=request.getAttribute("medie") %>'/>
        <jsp:setProperty name="studentBean" property="credite" value='<%=request.getAttribute("credite") %>'/>

        Actualizati datele despre student:
        <form action="./edit-student" method=post>
            Nume: <input type="text" name="nume" value=<jsp:getProperty name="studentBean" property="nume"/> />
            <br />
            Prenume: <input type="text" name="prenume" value=<jsp:getProperty name="studentBean" property="prenume"/> />
            <br />
            Varsta: <input type="number" name="varsta" value=<jsp:getProperty name="studentBean" property="varsta"/> />
            <br />
            Grupa: <input type="text" name="grupa" value=<jsp:getProperty name="studentBean" property="grupa"/> />
            <br />
            Credite: <input type="number" name="credite" value=<jsp:getProperty name="studentBean" property="credite"/> />
            <br />
            Medie: <input type="number" step="0.01" name="medie" value=<jsp:getProperty name="studentBean" property="medie"/> />
            <br />
            <br />
            <input type="hidden" name="id" value='<%=request.getAttribute("id") %>' />
            <button type="submit" name="submit">Trimite</button>
        </form>
        <form action="./index.jsp">
            <button type="submit" name="submit">Inapoi la prima pagina</button>
        </form>
    </body>
</html>