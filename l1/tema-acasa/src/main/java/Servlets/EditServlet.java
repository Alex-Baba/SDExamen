package Servlets;

import Database.StudentCRUD;
import beans.StudentBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class EditServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StudentCRUD operatii = new StudentCRUD();
        StudentBean bean = null;
        int id = Integer.parseInt(req.getParameter("id"));
        try {
            bean = operatii.getStudentById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(bean == null) {
            resp.sendError(404, "Nu a fost gasit niciun student in baza de date!");
            return;
        }
        req.setAttribute("id", id);
        req.setAttribute("nume", bean.getNume());
        req.setAttribute("prenume", bean.getPrenume());
        req.setAttribute("varsta", bean.getVarsta());
        req.setAttribute("credite", bean.getCredite());
        req.setAttribute("medie", bean.getMedie());
        req.setAttribute("grupa", bean.getGrupa());

        req.getRequestDispatcher("./edit-formular.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));
        String grupa = request.getParameter("grupa");
        int credite = Integer.parseInt(request.getParameter("credite"));
        double medie = Double.parseDouble(request.getParameter("medie"));

        int id = Integer.parseInt(request.getParameter("id"));

        StudentCRUD crud = new StudentCRUD();

        // creare bean si populare cu date
        StudentBean bean = new StudentBean();
        bean.setNume(nume);
        bean.setPrenume(prenume);
        bean.setVarsta(varsta);
        bean.setMedie(medie);
        bean.setCredite(credite);
        bean.setGrupa(grupa);

        try {
            crud.updateStudent(id, bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // se trimit datele primite si anul nasterii catre o alta pagina JSP pentru afisare
        request.setAttribute("nume", nume);
        request.setAttribute("prenume", prenume);
        request.setAttribute("varsta", varsta);
        request.setAttribute("credite", credite);
        request.setAttribute("medie", medie);
        request.setAttribute("grupa", grupa);
        request.setAttribute("id", id);

        request.getRequestDispatcher("./info-student.jsp").forward(request, response);
    }
}
