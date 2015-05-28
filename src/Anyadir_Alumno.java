import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zubiri.matriculas.Alumno;

/**
 * Servlet implementation class Anyadir_Alumno
 */
public class Anyadir_Alumno extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Anyadir_Alumno() {
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
		
		Alumno nuevoAlumno = new Alumno(request.getParameter("dni"),"","",0,"");

		nuevoAlumno.setNombre(request.getParameter("nombre"));
		nuevoAlumno.setApellido(request.getParameter("apellido"));
		nuevoAlumno.setAnyoInscripcion(Integer.parseInt(request.getParameter("anyo_inscripcion")));
		nuevoAlumno.setCiclo(request.getParameter("ciclo"));
		System.out.println("Nuevo alumno: "+nuevoAlumno.getDni()+" "+nuevoAlumno.getNombre()+" "+nuevoAlumno.getApellido()+" "+nuevoAlumno.getAnyoInscripcion()+" "+nuevoAlumno.getCiclo());
		
		Connection con = null;	
		Statement sentenciaPersona = null;
		Statement sentenciaAlumno = null;
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentenciaPersona = con.createStatement();
			sentenciaAlumno = con.createStatement();
			
			String sqlPersona;
			String sqlAlumno;
			
			System.out.println("INSERT INTO personas VALUES (\""+
					nuevoAlumno.getDni()+"\",\""+
					nuevoAlumno.getNombre()+"\",\""+
					nuevoAlumno.getApellido()+"\")");
			
			sqlPersona="INSERT INTO personas VALUES (\""+
					nuevoAlumno.getDni()+"\",\""+
					nuevoAlumno.getNombre()+"\",\""+
					nuevoAlumno.getApellido()+"\")";

			System.out.println("INSERT INTO alumnos VALUES (\""+
					nuevoAlumno.getDni()+"\","+
					nuevoAlumno.getAnyoInscripcion()+",\""+
					nuevoAlumno.getCiclo()+"\")");
			
			sqlAlumno="INSERT INTO alumnos VALUES (\""+
					nuevoAlumno.getDni()+"\","+
					nuevoAlumno.getAnyoInscripcion()+",\""+
					nuevoAlumno.getCiclo()+"\")";
			
			int crearPersona = sentenciaPersona.executeUpdate(sqlPersona);
			int crearAlumno = sentenciaAlumno.executeUpdate(sqlAlumno);
			
			System.out.println("Valor crear: " + crearPersona);
			if (crearPersona == 1) {
				if (crearAlumno == 1) {
					response(response, nuevoAlumno.getDni());
				}
			} else {
				response(response,"Error al crear el alumno", "relleno");
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
		out.println("<a href='alumnos.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	//Muestra alumno creado
	private void response(HttpServletResponse response, String dni) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<form name=\"buscar_alumno\" method=\"post\" onsubmit=\"return validacion_busqueda_alumno()\" action=\"Buscar_Alumno\">");
				out.println("<label> Alumno creado: </label>");
				out.println("<input name='alumno' type='text' value='" + dni + "' hidden='true'/> <br>");
				out.println("<input name=\"alumno\" type=\"text\" value=\""+dni+"\" placeholder=\""+dni+"\" disabled/>");
				out.println("<input type=\"submit\" id=\"submit\" value=\"Mostrar\">");
				out.println("</form>");
		out.println("<a href='alumnos.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
}
