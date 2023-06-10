<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Formular student</title>
        <meta charset="UTF-8">
    </head>
    <body>
        <h3>Formular student</h3>
        Introduceti datele despre student:
        <form action="./process-student" method=post>
            Nume: <input type="text" name="nume" />
            <br />
            Prenume: <input type="text" name="prenume" />
            <br />
            Varsta: <input type="number" name="varsta" />
            <br />
            Grupa: <input type="text" name="grupa" />
            <br />
            Credite: <input type="number" name="credite" />
            <br />
            Medie: <input type="number" step="0.01" name="medie" />
            <br />
            <br />
            <button type="submit" name="submit">Trimite</button>
        </form>
        <form action="./index.jsp">
            <button type="submit" name="submit">Inapoi la prima pagina</button>
        </form>
    </body>
</html>