/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package REST;

import org.json.JSONObject;
import Dominio.Cliente;
import Dominio.Reserva;
import Dominio.Usuario;
import Dominio.Vehiculo;
import Persistencia.ClienteFacadeLocal;
import Persistencia.ReservaFacadeLocal;
import Persistencia.UsuarioFacadeLocal;
import Persistencia.VehiculoFacadeLocal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author fernandozamora
 */
@Path("generic")
public class GenericResource {

    VehiculoFacadeLocal vehiculoFacade = lookupVehiculoFacadeLocal();
    ClienteFacadeLocal clienteFacade = lookupClienteFacadeLocal();
    ReservaFacadeLocal reservaFacade = lookupReservaFacadeLocal();
    UsuarioFacadeLocal usuarioFacade = lookupUsuarioFacadeLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    @Path("login/{nif}")
    @GET
    public Response logUsuario(@PathParam("nif") String nif, @HeaderParam("Authorization") String clave) {

        Response.ResponseBuilder respuesta = Response.status(Response.Status.ACCEPTED);
        respuesta.header("Access-Control-Allow-Origin", "*"); // valor de cabecera para evitar problema CORS
        respuesta.header("Access-Control-Expose-Headers", "*");

        Usuario usuario = usuarioFacade.comprobarUsuario(nif, clave);

        if (usuario != null) {
            return respuesta.status(200).build();
        } else {
            return respuesta.status(401).build();
        }
    }

    @Path("reservas/{nif}")
    @GET
    @Produces("application/json")
    public Response getReservas(@PathParam("nif") String nif) {
        Response.ResponseBuilder respuesta = Response.status(Response.Status.ACCEPTED);
        respuesta.header("Access-Control-Allow-Origin", "*"); // valor de cabecera para evitar problema CORS
        respuesta.header("Access-Control-Expose-Headers", "*"); // el navegador acceda a todas las cabeceras 
        respuesta.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        respuesta.header("Access-Control-Allow-Headers", "Origin,  X-Requested-With,  X-HTTP-Method-Override,  Content-Type, Accept, Authorization");

        respuesta.header("Access-Control-Allow-Credentials", "true");

        respuesta.type("application/json");

        List<Reserva> reservas = reservaFacade.getReservas(clienteFacade.find(nif));

        if (reservas == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Reserva[] arrayReservas = new Reserva[reservas.size()];
        //JSONObject [] arrayReservas = new JSONObject[reservas.size()];
        for (int i = 0; i < arrayReservas.length; i++) {
            arrayReservas[i] = reservas.get(i);
            arrayReservas[i].getNif().setReservaList(null);
            arrayReservas[i].getNif().setUsuario(null);
        }

        respuesta.entity(arrayReservas);
        return respuesta.build();

    }

    @Path("{id}")
    @GET
    @Produces("application/json")
    public Response getReserva(@PathParam("id") int id) {

        Response.ResponseBuilder respuesta = Response.status(Response.Status.ACCEPTED);
        respuesta.header("Access-Control-Allow-Origin", "*"); // valor de cabecera para evitar problema CORS
        respuesta.header("Access-Control-Expose-Headers", "*"); // para que el navegador acceda a todas las cabeceras        
        respuesta.type("application/json");
        try {

            Reserva reserva = (Reserva) reservaFacade.find(id);
            reserva.getNif().setReservaList(null);
            reserva.getNif().setUsuario(null);
            respuesta.entity(reserva);
            return respuesta.build();

        } catch (Exception e) {
            respuesta.status(Response.Status.INTERNAL_SERVER_ERROR);
            return respuesta.build();
        }

    }

    @Path("borrar/{id}")
    @DELETE
    public Response borraReserva(@PathParam("id") int id) {
        Response.ResponseBuilder respuesta = Response.noContent();
        // valor de cabecera para evitar problema CORS        
        respuesta.header("Access-Control-Allow-Origin", "*");
        // para que el navegador acceda a todas las cabeceras        
        respuesta.header("Access-Control-Expose-Headers", "*");//  Cabeceras añadidas para intentar evitar el bloqueo CORS. NO FUNCIONA, ya que delete, como put,
        //son bloqueadas. Se deja ya que puede ser interesante conocer mas cabeceras
        //  Se dejan, de igual manera, en el resto de operaciones a partir de aqui        
        respuesta.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        respuesta.header("Access-Control-Allow-Headers", "Origin,  X-Requested-With,  X-HTTP-Method-Override,  Content-Type, Accept, Authorization");

        respuesta.header("Access-Control-Allow-Credentials", "true");

        try {
            Reserva reserva = (Reserva) reservaFacade.find(id);
            reservaFacade.remove(reserva);
            respuesta.status(Response.Status.NO_CONTENT);
            return respuesta.build();
        } catch (Exception e) {
            respuesta.status(Response.Status.INTERNAL_SERVER_ERROR);
            return respuesta.build();
        }

    }

    @Path("modifica/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificaReserva(JsonObject reservaMod, @PathParam("id") int id) {
        Response.ResponseBuilder respuesta = Response.noContent();        // valor de cabecera para evitar problema CORS        
        respuesta.header("Access-Control-Allow-Origin", "*");        // para que el navegador acceda a todas las cabeceras        
        respuesta.header("Access-Control-Expose-Headers", "*");
        respuesta.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        respuesta.header("Access-Control-Allow-Headers", "Origin,  X-Requested-With,  X-HTTP-Method-Override,  Content-Type, Accept, Authorization");
        respuesta.header("Access-Control-Allow-Credentials", "true");        // Tipo de dato devuelto        //
        respuesta.type("text/plain");
        try {
            Reserva reserva = reservaFacade.find(id);

            String fechaIni = reservaMod.getString("fechainicioalquiler");
            String fechaFin = reservaMod.getString("fechafinalquiler");
            

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date fechaIniDate = null;
            Date fechaFinDate = null;
            try {
                fechaIniDate = formato.parse(fechaIni);
                fechaFinDate = formato.parse(fechaFin);
                
            } catch (Exception e) {

            }

            reserva.setFechainicioalquiler(fechaIniDate);
            reserva.setFechafinalquiler(fechaFinDate);
            reservaFacade.edit(reserva);
            respuesta.status(Response.Status.NO_CONTENT);
            return respuesta.build();
        } catch (Exception e) {
            respuesta.status(Response.Status.INTERNAL_SERVER_ERROR);
            return respuesta.build();
        }

    }

    @Path("anade")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response anadeReserva(JsonObject reserva) {
        
        Response.ResponseBuilder respuesta = Response.noContent();
        // valor de cabecera para evitar problema CORS        
        respuesta.header("Access-Control-Allow-Origin", "*");
        // para que el navegador acceda a todas las cabeceras        
        respuesta.header("Access-Control-Expose-Headers", "*");
        respuesta.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        respuesta.header("Access-Control-Allow-Headers", "Origin,  X-Requested-With,  X-HTTP-Method-Override,  Content-Type, Accept, Authorization");
        respuesta.header("Access-Control-Allow-Credentials", "true");
        
        
        
        try {
            
            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setEjecutada('F');
            
            
            nuevaReserva.setNif(clienteFacade.find(reserva.getJsonObject("nif").getString("nif")));
            nuevaReserva.setMatricula(vehiculoFacade.find(reserva.getJsonObject("matricula").getString("matricula")));
            
            

            String fechaIni = reserva.getString("fechainicioalquiler");
            String fechaFin = reserva.getString("fechafinalquiler");
            System.out.println(fechaIni);
            
            
            
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaIniDate = null;
            Date fechaFinDate = null;
            Date fechaReservaDate = new Date();
            try {
                fechaIniDate = formato.parse(fechaIni);
                fechaFinDate = formato.parse(fechaFin);
                
            } catch (Exception e) {
                System.out.println(e);
            }
            

            nuevaReserva.setFechainicioalquiler(fechaIniDate);
            nuevaReserva.setFechafinalquiler(fechaFinDate);
            nuevaReserva.setFechareserva(fechaReservaDate);
            
                
            reservaFacade.creaReserva(nuevaReserva);
            
            respuesta.status(Response.Status.NO_CONTENT);
            return respuesta.build();
        } catch (Exception E) {
            respuesta.status(Response.Status.INTERNAL_SERVER_ERROR);
            return respuesta.build();
        }
    }

    @Path("vehiculos/{fechaInicio}/{fechaFin}")
    @GET
    @Produces("application/json")
    public Response getVehiculos(@PathParam("fechaInicio") String fechaInicio, @PathParam("fechaFin") String fechaFin) {

        Response.ResponseBuilder respuesta = Response.status(Response.Status.ACCEPTED);
        respuesta.header("Access-Control-Allow-Origin", "*"); // valor de cabecera para evitar problema CORS
        respuesta.header("Access-Control-Expose-Headers", "*"); // el navegador acceda a todas las cabeceras 
        respuesta.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        respuesta.header("Access-Control-Allow-Headers", "Origin,  X-Requested-With,  X-HTTP-Method-Override,  Content-Type, Accept, Authorization");

        respuesta.header("Access-Control-Allow-Credentials", "true");

        respuesta.type("application/json");
        
        Date fechaI=null;
        Date fechaF= null;
        
        
        try{
         
        fechaI=new SimpleDateFormat("yyyy-MM-dd").parse(fechaInicio);
        fechaF=new SimpleDateFormat("yyyy-MM-dd").parse(fechaFin);
        }catch(Exception e){
            
        } 
        
    

        List<Vehiculo> listaVehiculosTotal = vehiculoFacade.findAll();
        
        

        String[] listaMatriculasReservados = reservaFacade.getReservasFecha(fechaI, fechaF);
        

        if (listaMatriculasReservados == null) {

            respuesta.entity(listaVehiculosTotal);
            return respuesta.build();

        }

        
        List<Vehiculo> listaVehiculosReservados = vehiculoFacade.getVehiculosPorMatriculas(listaMatriculasReservados);

        //Se filtran de los totales los reservados y se devuelve la lista resultado
        for (Vehiculo auxVehiculoReservado : listaVehiculosReservados) {

            if (listaVehiculosTotal.contains(auxVehiculoReservado)) {

                listaVehiculosTotal.remove(auxVehiculoReservado);

            }

        }
        
        Vehiculo[] arrayVehiculosTotal = new Vehiculo[listaVehiculosTotal.size()];
        //JSONObject [] arrayReservas = new JSONObject[reservas.size()];
        for (int i = 0; i < arrayVehiculosTotal.length; i++) {
            arrayVehiculosTotal[i] = listaVehiculosTotal.get(i);
        }
        
        

        respuesta.entity(arrayVehiculosTotal);
        return respuesta.build();

    }

    private UsuarioFacadeLocal lookupUsuarioFacadeLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (UsuarioFacadeLocal) c.lookup("java:global/ServicioReservas/UsuarioFacade!Persistencia.UsuarioFacadeLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private ReservaFacadeLocal lookupReservaFacadeLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ReservaFacadeLocal) c.lookup("java:global/ServicioReservas/ReservaFacade!Persistencia.ReservaFacadeLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private ClienteFacadeLocal lookupClienteFacadeLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ClienteFacadeLocal) c.lookup("java:global/ServicioReservas/ClienteFacade!Persistencia.ClienteFacadeLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private VehiculoFacadeLocal lookupVehiculoFacadeLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (VehiculoFacadeLocal) c.lookup("java:global/ServicioReservas/VehiculoFacade!Persistencia.VehiculoFacadeLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
