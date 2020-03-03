/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia;

import Dominio.Cliente;
import Dominio.Reserva;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author fernandozamora
 */
@Stateless
public class ReservaFacade extends AbstractFacade<Reserva> implements ReservaFacadeLocal {
    @PersistenceContext(unitName = "ServicioReservasPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReservaFacade() {
        super(Reserva.class);
    }
    
    @Override
    
    public List<Reserva> getReservas(Cliente nif) {
        
        
        Query query = em.createNamedQuery("Reserva.findByNif");
        query.setParameter("nif", nif);
        List<Reserva> reservas = (List<Reserva>) query.getResultList();
        
        
        if (reservas != null){
            return reservas;
        }else{
            return null;
        }
    }
    
    @Override
    //Devuelve las matriculas de los vehiculos alquilados entre las fechas indicadas
    public String[] getReservasFecha(Date fechaInicio, Date fechaFin) {

        Query query = em.createNamedQuery("Reserva.findByFechas");
        query.setParameter("fechainicioalquiler", fechaInicio);
        query.setParameter("fechafinalquiler", fechaFin);
        List<Reserva> reservas = (List<Reserva>) query.getResultList();

        if (reservas != null) {

            String[] arrayDeMatriculas = new String[reservas.size()];

            for (Reserva i : reservas) {

                arrayDeMatriculas[reservas.indexOf(i)] = i.getMatricula().getMatricula();

            }

            return arrayDeMatriculas;
        } else {
            return null;
        }
    }
    
    @Override
    //Crea una reserva con los parametros introducidos
    public Reserva creaReserva(Reserva reserva) {

        try {
        
            Query query = em.createNamedQuery("Reserva.findAll");
            List<Reserva> reservas = query.getResultList();

            int numId;

            if (reservas != null){
                numId = reservas.size() + 1;
            } else {
                return null;
            }

            

            reserva.setIdreserva(numId);
            
            getEntityManager().persist(reserva);
            return null;
           
        } catch (Exception e) {
            
            return null;
        }

    }
    
}
