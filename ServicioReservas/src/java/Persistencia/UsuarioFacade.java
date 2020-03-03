/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia;

import Dominio.Usuario;
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
public class UsuarioFacade extends AbstractFacade<Usuario> implements UsuarioFacadeLocal {
    @PersistenceContext(unitName = "ServicioReservasPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    @Override
    //Comprueba que existe un usuario con el nombre y clave especificado y lo devuelve. Si no esta devuelve null
    public Usuario comprobarUsuario(String nif, String clave) {
        
        Query query = em.createNamedQuery("Usuario.findByNombreYNif");
        query.setParameter("nif", nif);
        query.setParameter("password", clave);
        List<Usuario> usuario = (List<Usuario>) query.getResultList();
        
        if (usuario != null && !usuario.isEmpty()){
            return usuario.get(0);
        }else{
            return null;
        }
        
    }
    
}
