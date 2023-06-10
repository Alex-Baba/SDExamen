import beans.StudentBean;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.Year;

public class EditServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File file = new File("/home/student/opt/1307A/Popescu Ion/student.xml");
// se returneaza un raspuns HTTP de tip 404 in cazul in care nu se gaseste fisierul cu date
        if (!file.exists()) {
            resp.sendError(404, "Nu a fost gasit niciun student serializat pe disc!");
            return;
        }
        XmlMapper xmlMapper = new XmlMapper();
        StudentBean bean = xmlMapper.readValue(file, StudentBean.class);

        req.setAttribute("nume", bean.getNume());
        req.setAttribute("prenume", bean.getPrenume());
        req.setAttribute("varsta", bean.getVarsta());

        req.getRequestDispatcher("./edit-formular.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // se citesc parametrii din cererea de tip POST
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));

        //procesarea datelor - calcularea anului nasterii
        int anCurent = Year.now().getValue();
        int anNastere = anCurent - varsta;

        // initializare serializator Jackson
        XmlMapper mapper = new XmlMapper();

        // creare bean si populare cu date
        StudentBean bean = new StudentBean();
        bean.setNume(nume);
        bean.setPrenume(prenume);
        bean.setVarsta(varsta);

        // serializare bean sub forma de string XML
        File file = new File("/home/student/opt/1307A/Popescu Ion/student.xml");
        file.createNewFile();
        mapper.writeValue(file, bean);

        // se trimit datele primite si anul nasterii catre o alta pagina JSP pentru afisare
        request.setAttribute("nume", nume);
        request.setAttribute("prenume", prenume);
        request.setAttribute("varsta", varsta);
        request.setAttribute("anNastere", anNastere);
        request.getRequestDispatcher("./info-student.jsp").forward(request, response);
    }
}
