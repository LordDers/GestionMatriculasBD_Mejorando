

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

import com.zubiri.matriculas.Asignatura;
import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Modificar_Asignatura
 */
public class Modificar_Asignatura extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Modificar_Asignatura() {
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
		Statement sentenciaUpdate = null;
		
		System.out.println("Empieza modificando");

		Boolean confirmacion = Boolean.parseBoolean(request.getParameter("confirmacion"));

		Profesor profesor = new Profesor("","","","","");
		String referencia = request.getParameter("asignatura");
		
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			sentenciaUpdate = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + referencia);
			
			sql="SELECT profesores.dni, persona.nombre AS 'profesor', persona.apellido, profesores.titulacion, profesores.departamento, asignaturas.nombre AS 'asignatura', asignaturas.creditos "+
					"FROM (persona INNER JOIN profesores ON persona.dni = profesores.dni) "+
					"INNER JOIN asignaturas ON profesores.dni = asignaturas.dni_profesor "+
					"WHERE asignaturas.nombre=\""+referencia+"\"";
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			while (buscar.next()) {
				cont++;
			}
			if (cont > 0) {
				System.out.println("Contador: " + cont);
				if (confirmacion != true) {
					formulario_modificar(response,request.getParameter("asignatura"));
				} else {
					profesor.setDni(request.getParameter("dniProfesor"));
					
					Asignatura asignatura = new Asignatura("", 0, profesor);
					
					asignatura.setNombre(request.getParameter("asignatura"));
					asignatura.setCreditos(Integer.parseInt(request.getParameter("creditos")));
					
					String id = request.getParameter("id");
					
					String cambios="";
					cambios="id_asignatura = "+id+",";
					cambios+=" nombre = \""+asignatura.getNombre()+"\",";
					cambios+=" creditos = "+asignatura.getCreditos()+",";
					cambios+=" dni_profesor = \""+asignatura.getProfesor().getDni()+"\"";
					
					try {
						// Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
						// Open a connection
						con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);			        
						sentencia = con.createStatement();

						System.out.println("UPDATE asignaturas SET "+cambios+" WHERE nombre=\""+asignatura.getNombre()+"\"");
						
						String sqlUpdate;
						sqlUpdate="UPDATE asignaturas SET "+cambios+" WHERE nombre=\""+asignatura.getNombre()+"\"";

						int update = sentenciaUpdate.executeUpdate(sqlUpdate);
						
						System.out.println("Valor actualizar: " + update);
						if (update == 1) {
							response(response, "Se ha modificado la asignatura " + asignatura.getNombre() + ".<br>" + cambios);
						} else {
							response(response, "¡Error! No se ha modificado la asignatura, compruebe el nombre: " + asignatura.getNombre());
						}
						con.close();
					} catch(ArrayIndexOutOfBoundsException e) {
						response(response, "No se encontró la asignatura");
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				response(response, "No se encontró la asignatura");
			}
			con.close();
		
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "No se encontro la asignatura");
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
		out.println("<a href='asignaturas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	private void formulario_modificar(HttpServletResponse response, String referencia) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		System.out.println("Se está modificando la asignatura: " + referencia);

		Connection con = null;	
		Statement sentencia = null;
		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: "+referencia);
			sql="SELECT profesores.dni, persona.nombre AS 'profesor', persona.apellido, profesores.titulacion, profesores.departamento, asignaturas.id_asignatura, asignaturas.nombre AS 'asignatura', asignaturas.creditos "+
					"FROM (persona INNER JOIN profesores ON persona.dni = profesores.dni) "+
					"INNER JOIN asignaturas ON profesores.dni = asignaturas.dni_profesor "+
					"WHERE asignaturas.nombre=\""+referencia+"\"";
			ResultSet buscar = sentencia.executeQuery(sql);

			Profesor profesor = new Profesor("", "", "", "", "");
			Asignatura asignatura = new Asignatura("", 0, profesor);
			
			int id = -1;		
			while (buscar.next()) {
				profesor = new Profesor(buscar.getString("dni"), "", "", "", "");
				profesor.setNombre(buscar.getString("profesor"));
				profesor.setApellido(buscar.getString("apellido"));
				profesor.setTitulacion(buscar.getString("titulacion"));
				profesor.setDepartamento(buscar.getString("departamento"));
				
				asignatura = new Asignatura("", 0, profesor);
				asignatura.setNombre(buscar.getString("asignatura"));
				asignatura.setCreditos(buscar.getInt("creditos"));
				id = buscar.getInt("id_asignatura");
				
				System.out.println("Asignatura: " + asignatura.getNombre());
				System.out.println("Id asignatura: " + id);
				System.out.println("Dni Profesor: " + profesor.getDni());
				System.out.println("Nombre: " + profesor.getNombre());
				System.out.println("Apellido: " + profesor.getApellido());
				System.out.println("Créditos: " + asignatura.getCreditos());
			}

			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<head>");
				out.println("<title> Modificar asignatura </title>");
				out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
			out.println("</head>");
			out.println("<body>");
			out.println("<fieldset>	<legend> Modificar Profesor " + asignatura.getNombre() + "</legend>");
				out.println("<form name='modificar_asignatura' method='post' onsubmit='return validacion_modificar_alumno()' action='Modificar_Asignatura'>");
					out.println("<input name='gestion' hidden='true' type='text' value='modificar_asignatura'/>");
					out.println("<input name='asignatura' type='text' value='" + asignatura.getNombre() + "' hidden='true'/> <br>");
					out.println("<label>Asignatura: </label> <input type='text' value='" + asignatura.getNombre() + "' disabled/>");
					out.println("<input name='id' type='text' value='" + id + "' hidden='true'/> <br>");
					out.println("<label> Id Asignatura: </label> <input name='id' type='text' value='" + id + "' disabled/> <br>");
					out.println("<label>Créditos: </label> <input name='creditos' type='text' id='nombre' value='" + asignatura.getCreditos() + "' /> <br>");
					out.println("<label>DNI del profesor: </label> <input name='dniProfesor' type='text' id='apellido' value='"+asignatura.getProfesor().getDni()+"' /> <br>");
					out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\" value='true'></input>");
					out.println("<input type='submit' id='submit' value='Modificar'>");
				out.println("</form>");
			out.println("</fieldset>");
			out.println("<br> <a href='asignaturas.html'> <button> Volver </button> </a>");
			out.println("</body>");
			out.println("</html>");

			con.close();

		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro el vehiculo");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
