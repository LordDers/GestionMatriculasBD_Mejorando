

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Anyadir_Profesor
 */
public class Anyadir_Profesor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Anyadir_Profesor() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println("Empieza añadiendo");	
		
		Profesor nuevoProfesor = new Profesor(request.getParameter("dni"),"","","","");
		
		//nuevoProfesor.setDni(request.getParameter("dni"));
		nuevoProfesor.setNombre(request.getParameter("nombre"));
		nuevoProfesor.setApellido(request.getParameter("apellido"));
		nuevoProfesor.setTitulacion(request.getParameter("titulacion"));
		nuevoProfesor.setDepartamento(request.getParameter("departamento"));
		System.out.println("Nuevo profesor: "+nuevoProfesor.getDni()+" "+nuevoProfesor.getNombre()+" "+nuevoProfesor.getApellido()+" "+nuevoProfesor.getTitulacion()+" "+nuevoProfesor.getDepartamento());
		
		Connection con = null;	
		Statement sentenciaPersona = null;
		Statement sentenciaProfesor = null;
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentenciaPersona = con.createStatement();
			sentenciaProfesor = con.createStatement();
			
			String sqlPersona;
			String sqlProfesor;
			
			System.out.println("INSERT INTO persona VALUES (\""+
					nuevoProfesor.getDni()+"\",\""+
					nuevoProfesor.getNombre()+"\",\""+
					nuevoProfesor.getApellido()+"\")");
			
			sqlPersona="INSERT INTO persona VALUES (\""+
					nuevoProfesor.getDni()+"\",\""+
					nuevoProfesor.getNombre()+"\",\""+
					nuevoProfesor.getApellido()+"\")";

			System.out.println("INSERT INTO profesores VALUES (\""+
																nuevoProfesor.getDni()+"\",\""+
																nuevoProfesor.getTitulacion()+"\",\""+
																nuevoProfesor.getDepartamento()+"\")");
			
			sqlProfesor="INSERT INTO profesores VALUES (\""+
					nuevoProfesor.getDni()+"\",\""+
					nuevoProfesor.getTitulacion()+"\",\""+
					nuevoProfesor.getDepartamento()+"\")";
			
			int crearPersona = sentenciaPersona.executeUpdate(sqlPersona);
			int crearProfesor = sentenciaProfesor.executeUpdate(sqlProfesor);
			
			System.out.println("Valor crear: " + crearPersona);
			if (crearPersona == 1) {
				if (crearProfesor == 1) {
					response(response, nuevoProfesor.getDni());
				}
			} else {
				response(response,"Error al crear el profesor", "relleno");
			}	
			con.close();    
			
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro el vehiculo");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// Respuesta simple
	private void response(HttpServletResponse response, String msg, String relleno) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Respuesta </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body>");				
		out.println("<p>" + msg + "</p>");
		out.println("<a href='profesores.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	//muestra de alumno matriculado
	private void response(HttpServletResponse response, String dni) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<form name=\"buscar_profesor\" method=\"post\" onsubmit=\"return validacion_busqueda_alumno()\" action=\"Buscar_Profesor\">");
				out.println("<label> Profesor creado: </label>");
				out.println("<input name='profesor' type='text' value='" + dni + "' hidden='true'/> <br>");
				out.println("<input name=\"profesor\" type=\"text\" value=\""+dni+"\" placeholder=\""+dni+"\" disabled/>");
				out.println("<input type=\"submit\" id=\"submit\" value=\"Mostrar\">");
				out.println("</form>");
		out.println("<a href='profesores.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
}
