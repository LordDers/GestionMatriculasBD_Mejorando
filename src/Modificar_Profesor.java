

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
 * Servlet implementation class Modificar_Profesor
 */
public class Modificar_Profesor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Modificar_Profesor() {
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
		
		System.out.println("Empieza modificando");

		Boolean confirmacion = Boolean.parseBoolean(request.getParameter("confirmacion"));

		Profesor profesor = new Profesor(request.getParameter("dniProfesor"), "", "", "", "");
		
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
				if (confirmacion != true) {
					formulario_modificar(response,request.getParameter("dniProfesor"));
				} else {
					profesor.setDni(request.getParameter("dniProfesor"));
					profesor.setNombre(request.getParameter("nombre"));
					profesor.setApellido(request.getParameter("apellido"));
					profesor.setTitulacion(request.getParameter("titulacion"));
					profesor.setDepartamento(request.getParameter("departamento"));
					
					String cambiosPersona="";					
					String cambiosProfesor="";
					
					cambiosProfesor="dni = \""+profesor.getDni()+"\",";
					cambiosProfesor+=" titulacion = \""+profesor.getTitulacion()+"\",";
					cambiosProfesor+=" departamento = \""+profesor.getDepartamento()+"\"";
					
					cambiosPersona="dni = \""+profesor.getDni()+"\",";
					cambiosPersona+=" nombre = \""+profesor.getNombre()+"\",";
					cambiosPersona+=" apellido = \""+profesor.getApellido()+"\"";
					
					try {
						// Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
						// Open a connection
						con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);			        
						sentencia = con.createStatement();
						
						System.out.println("UPDATE persona SET "+cambiosPersona+" WHERE dni=\""+profesor.getDni()+"\"");
						System.out.println("UPDATE profesores SET "+cambiosProfesor+" WHERE dni=\""+profesor.getDni()+"\"");
						
						String sqlUpdate;
						sqlUpdate="UPDATE persona SET "+cambiosPersona+" WHERE dni=\""+profesor.getDni()+"\"";
						String sqlProfesor="UPDATE profesores SET "+cambiosProfesor+" WHERE dni=\""+profesor.getDni()+"\"";
						
						int updateProfesor = sentencia.executeUpdate(sqlProfesor);
						int updatePersona = sentencia.executeUpdate(sqlUpdate);
						
						System.out.println("Valor actualizar: " + updatePersona);
						if (updatePersona == 1) {
							if (updateProfesor == 1) {
								response(response, "Se ha modificado el profesor con DNI " + profesor.getDni() + ".<br>" + cambiosPersona);
							}
						} else {
							response(response, "¡Error! No se ha modificado el profesor, compruebe el DNI: " + profesor.getDni());
						}
						con.close();
					} catch(ArrayIndexOutOfBoundsException e) {
						response(response, "No se encontró el profesor");
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				response(response, "No se encontró el profesor");
			}
			con.close();
		
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "No se encontro el profesor");
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
	
	private void formulario_modificar(HttpServletResponse response, String referencia) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		System.out.println("Se está modificando el profesor con DNI: " + referencia);

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
			//sql="SELECT matricula, marca FROM coches WHERE matricula='"+referencia+"'";
			sql="SELECT persona.dni, persona.nombre, persona.apellido, profesores.titulacion, profesores.departamento FROM persona INNER JOIN profesores ON persona.dni = profesores.dni WHERE persona.dni=\""+referencia+"\"";
			ResultSet buscar = sentencia.executeQuery(sql);

			Profesor profesor = new Profesor("", "", "", "", "");
					
			while (buscar.next()) {
				profesor = new Profesor(buscar.getString("dni"), "", "", "", "");
				profesor.setNombre(buscar.getString("nombre"));
				profesor.setApellido(buscar.getString("apellido"));
				profesor.setTitulacion(buscar.getString("titulacion"));
				profesor.setDepartamento(buscar.getString("departamento"));
				
				System.out.println("DNI: " + profesor.getDni());
				System.out.println("Nombre: " + profesor.getNombre());
				System.out.println("Apellido: " + profesor.getApellido());
				System.out.println("Titulacion: " + profesor.getTitulacion());
				System.out.println("Departamento: " + profesor.getDepartamento());
			}

			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<head>");
				out.println("<title> Modificar profesor </title>");
				out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
			out.println("</head>");
			out.println("<body>");
			out.println("<fieldset>	<legend> Modificar Profesor " + referencia + "</legend>");
				out.println("<form name='modificar_alumno' method='post' onsubmit='return validacion_modificar_alumno()' action='Modificar_Profesor'>");
					out.println("<input name='gestion' hidden='true' type='text' value='modificar_vehiculo'/>");
					out.println("<input name='dniProfesor' type='text' value='" + referencia + "' hidden='true'/> <br>");
					out.println("<label>DNI a modificar: </label> <input type='text' value='" + referencia + "' disabled/> <br>");
					out.println("<label>Nombre: </label> <input name='nombre' type='text' id='nombre' value='" + profesor.getNombre() + "' /> <br>");
					out.println("<label>Apellido: </label> <input name='apellido' type='text' id='apellido' value='"+profesor.getApellido()+"' /> <br>");
					out.println("<label>Titulación </label> <input name='titulacion' type='text' id='titulacion' value='"+profesor.getTitulacion()+"' /> <br>");
					out.println("<label> Departamento </label> <input name='departamento' type='text' id='ciclo' value='"+profesor.getDepartamento()+"' /> <br>");
					out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\" value='true'></input>");
					out.println("<input type='submit' id='submit' value='Modificar'>");
				out.println("</form>");
			out.println("</fieldset>");
			out.println("<br> <a href='profesores.html'> <button> Volver </button> </a>");
			out.println("<script type=\"text/javascript\">");
				out.println("function validacion_modificar_vehiculo_bd() { var a = document.forms[\"modificar_vehiculo\"][\"matriculanueva\"].value; if (validar_matricula_bd(a)) { return true; } else { return false; };}");
				out.println("function validar_matricula_bd(x) { if (x == null || x == \"\") { alert(\"Escribe la matrícula\"); console.log(\"Comprobación nula\"); return false; } else if(x.length!=7) { alert(\"No has introducido una matrícula válida (núm caracteres)\"); console.log(\"Error, número de caracteres matrícula\"); return false; } else { var expreg = /^[0-9]{4}[A-Z,a-z]{3}$/; if (expreg.test(x)) { return true; } else {	alert(\"La matrícula NO es correcta\");	console.log(\"Error en formato matrícula\"); return false; } } }");
			out.println("</script>");
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
