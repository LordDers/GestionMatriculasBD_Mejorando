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

import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Borrar_Profesor
 */
public class Borrar_Profesor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Borrar_Profesor() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
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
		
		Profesor profesor = new Profesor(request.getParameter("dniProfesor"),"","","","");

		Boolean confirmacion = Boolean.parseBoolean(request.getParameter("confirmacion"));

		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + profesor.getDni());
			sql="SELECT persona.dni, persona.nombre, persona.apellido, profesores.titulacion, profesores.departamento FROM persona INNER JOIN profesores ON persona.dni = profesores.dni WHERE persona.dni=\""+profesor.getDni()+"\"";
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			while (buscar.next()) {
				cont++;
			}
			if (cont > 0) {
				System.out.println("Contador: " + cont);
				if (confirmacion!=true) {
					confirmacion=false;
					response(response, "¿Seguro que quieres borrar el profesor?", profesor.getDni());
				} else {
					try {
						String sqlDelete;
						// Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
						// Open a connection
						con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);			        
						sentencia = con.createStatement();

						System.out.println("DELETE FROM persona where dni=\""+profesor.getDni()+"\"");
						System.out.println("DELETE FROM profesores where dni=\""+profesor.getDni()+"\"");
						
						sqlDelete="DELETE FROM persona where dni=\""+profesor.getDni()+"\"";
						String sqlProfesor="DELETE FROM profesores where dni=\""+profesor.getDni()+"\"";
						
						int borrarProfesor = sentencia.executeUpdate(sqlProfesor);
						int borrarPersona = sentencia.executeUpdate(sqlDelete);
						
						System.out.println("Valor borrar: " + borrarPersona);
						if (borrarPersona == 1) {
							if (borrarProfesor == 1) {
								response(response, "Se ha borrado el profesor " + profesor.getDni());
							}
						} else {
							response(response, "No se ha borrado el profesor, compruebe el DNI: " + profesor.getDni() + ".");
						}
						con.close();			    	
					} catch(ArrayIndexOutOfBoundsException e) {
						//response(response, "no se encontro el profesor");
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				response(response, "No se encontró el profesor");
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro el profesor");
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
		out.println("<a href='profesores.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	private void response(HttpServletResponse response,String msg ,String dni) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Borrar profesor </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body align='center'>");
		out.println("<p>" + msg + "</p>");
		out.println("<p>DNI: " + dni + "</p>");
		out.println("<form name=\"borrar_profesor\" method=\"post\" action=\"Borrar_Profesor\" style='margin-right: auto;'>");
			out.println("<input name='gestion' hidden='true' type='text'  value='borrar_profesor'/>");
			out.println("<input name=\"dniProfesor\" hidden=\"true\" type=\"text\"  value=" + dni + "></input>");
			out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\"  value='true'></input>");
			out.println("<p> <input type='submit' id='submit' value='Borrar'> </p>");
		out.println("</form>");
		out.println("<a href='profesores.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
}
