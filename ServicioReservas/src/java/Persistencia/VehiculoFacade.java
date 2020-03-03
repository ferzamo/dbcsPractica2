/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia;

import Dominio.Vehiculo;
import java.util.ArrayList;
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
public class VehiculoFacade extends AbstractFacade<Vehiculo> implements VehiculoFacadeLocal {
    @PersistenceContext(unitName = "ServicioReservasPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VehiculoFacade() {
        super(Vehiculo.class);
    }
    
    @Override
    //Devuelve una lista con los vehiculos que tengan una de las matriculas especificadas
    public List<Vehiculo> getVehiculosPorMatriculas(String[] matriculas) {
        
        List<Vehiculo> resultado = new ArrayList<Vehiculo>();
        
        for (String auxMatricula : matriculas) {
            
            Query query = em.createNamedQuery("Vehiculo.findByMatricula");
            query.setParameter("matricula", auxMatricula);
            List<Vehiculo> vehiculos = (List<Vehiculo>) query.getResultList();

            if (vehiculos != null){
                resultado.addAll(vehiculos);
            }
            
        }
        
        return resultado;
        
    }
    
}
