import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zubiri.matriculas.Alumno;

/**
 * Servlet implementation class Borrar_Alumno
 */
public class Borrar_Alumno extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Borrar_Alumno() {
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
		response.setContentType( "text/html; charset=iso-8859-1" );
		
		Connection con = null;	
		Statement sentencia = null;
		System.out.println("Borrando");
		
		Alumno alumno = new Alumno(request.getParameter("dniAlumno"),"","",0,"");

		Boolean confirmacion = Boolean.parseBoolean(request.getParameter("confirmacion"));

		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + alumno.getDni());
			sql="SELECT personas.dni, personas.nombre, personas.apellido, alumnos.ciclo, alumnos.anyo_inscripcion FROM personas INNER JOIN alumnos ON personas.dni = alumnos.dni WHERE personas.dni=\""+alumno.getDni()+"\"";
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			while (buscar.next()) {
				cont++;
			}
			if (cont > 0) {
				System.out.println("Contador: " + cont);
				if (confirmacion!=true) {
					confirmacion=false;
					response(response, "¿Seguro que quieres borrar el alumno?", alumno.getDni());
				} else {
					try {
						String sqlDelete;
						// Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
						// Open a connection
						con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);			        
						sentencia = con.createStatement();

						System.out.println("DELETE FROM personas where dni=\""+alumno.getDni()+"\"");
						System.out.println("DELETE FROM alumnos where dni=\""+alumno.getDni()+"\"");
						
						sqlDelete="DELETE FROM personas where dni=\""+alumno.getDni()+"\"";
						String sqlAlumno="DELETE FROM alumnos where dni=\""+alumno.getDni()+"\"";
						
						int borrarAlumno = sentencia.executeUpdate(sqlAlumno);
						int borrarPersona = sentencia.executeUpdate(sqlDelete);
						
						System.out.println("Valor borrar: " + borrarPersona);
						if (borrarPersona == 1) {
							if (borrarAlumno == 1) {
								response(response, "Se ha borrado el alumno " + alumno.getDni());
							}
						} else {
							response(response, "No se ha borrado el alumno, compruebe el DNI: " + alumno.getDni() + ".");
						}
						con.close();			    	
					} catch(ArrayIndexOutOfBoundsException e) {
						//response(response, "no se encontro el alumno");
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				response(response, "No se encontró el alumno");
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro el alumno");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void response(HttpServletResponse response,String msg) throws IOException {
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
	
	private void response(HttpServletResponse response,String msg ,String dni) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Borrar alumno </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body align='center'>");
		out.println("<p>" + msg + "</p>");
		out.println("<p>DNI: " + dni + "</p>");
		out.println("<form name=\"borrar_alumno\" method=\"post\" action=\"Borrar_Alumno\" style='margin-right: auto;'>");
			out.println("<input name='gestion' hidden='true' type='text'  value='borrar_alumno'/>");
			out.println("<input name=\"dniAlumno\" hidden=\"true\" type=\"text\"  value=" + dni + "></input>");
			out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\"  value='true'></input>");
			out.println("<p> <input type='submit' id='submit' value='Borrar'> </p>");
		out.println("</form>");
		out.println("<a href='alumnos.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}

}
