package Servlets;

import Database.StudentCRUD;
import beans.StudentBean;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Year;

public class ProcessStudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        // se citesc parametrii din cererea de tip POST
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));
        String grupa = request.getParameter("grupa");
        int credite = Integer.parseInt(request.getParameter("credite"));
        double medie = Double.parseDouble(request.getParameter("medie"));

        StudentCRUD crud = new StudentCRUD();

        // creare bean si populare cu date
        StudentBean bean = new StudentBean();
        bean.setNume(nume);
        bean.setPrenume(prenume);
        bean.setVarsta(varsta);
        bean.setMedie(medie);
        bean.setCredite(credite);
        bean.setGrupa(grupa);

        Integer id = 0;
        try {
            crud.addStudent(bean);
            id = crud.getStudentByFullName(bean.getNume(), bean.getPrenume()).getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // se trimit datele primite si anul nasterii catre o alta pagina JSP pentru afisare
        request.setAttribute("id", id);
        request.setAttribute("nume", nume);
        request.setAttribute("prenume", prenume);
        request.setAttribute("varsta", varsta);
        request.setAttribute("credite", credite);
        request.setAttribute("medie", medie);
        request.setAttribute("grupa", grupa);

        request.getRequestDispatcher("./info-student.jsp").forward(request, response);
    }
}