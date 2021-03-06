/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jass2125.igeo.core.dao;

import io.github.jass2125.igeo.core.entity.Ride;
import io.github.jass2125.igeo.core.entity.Ride_;
import io.github.jass2125.igeo.core.entity.UserPrincipal;
import io.github.jass2125.igeo.core.exceptions.EntityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author <a href="mailto:jair_anderson_bs@hotmail.com">Anderson Souza</a>
 * @since Jun 27, 2017 12:56:57 PM
 */
@Stateless
public class RideDao {

    @PersistenceContext
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<Ride> criteriaQuery;
    private CriteriaDelete<Ride> criteriaDelete;
    private Root<Ride> rootQuery;
    private Root<Ride> rootDelete;

    @PostConstruct
    public void init() {
        this.criteriaBuilder = em.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(Ride.class);
        this.criteriaDelete = criteriaBuilder.createCriteriaDelete(Ride.class);
        this.rootQuery = criteriaQuery.from(Ride.class);
        this.rootDelete = criteriaDelete.from(Ride.class);
    }

    @PreDestroy
    public void destroy() {
        this.em = null;
        this.criteriaBuilder = null;
        this.criteriaQuery = null;
        this.criteriaDelete = null;
        this.rootQuery = null;
        this.rootDelete = null;
    }

    public Ride save(Ride ride) throws EntityException {
        try {
            em.persist(ride);
            return ride;
        } catch (Exception e) {
            throw new EntityException(e, "Erro ao salvar entidade Ride");
        }
    }

    public Ride searchById(Long id) throws EntityException {
        try {
            return em.find(Ride.class, id);
        } catch (Exception e) {
            throw new EntityException(e, "Não foi possível consultar Ride!!");
        }
    }

    public Ride delete(Ride ride) throws EntityException {
        try {
            em.remove(ride);
//            criteriaDelete.where(criteriaBuilder.equal(rootDelete.get(Ride_.id), id));
//            int numberOfLines = em.createQuery(criteriaDelete).executeUpdate();
//            if (numberOfLines <= 0) {
//                throw new EntityException("Não foi possível excluir a entidade!!");
//            }
            return ride;
        } catch (Exception e) {
            throw new EntityException(e, "Não foi possível excluir a Carona!!");
        }
    }

    public Ride update(Ride ride) throws EntityException {
        try {
            return em.merge(ride);
        } catch (Exception e) {
            throw new EntityException(e, "Ocorreu um erro de sistema!!");
        }
    }

    public Set<Ride> findAll() throws EntityException {
        try {
            this.criteriaQuery.multiselect(
                    this.rootQuery.get(Ride_.id),
                    this.rootQuery.get(Ride_.date),
                    this.rootQuery.get(Ride_.departureTime)
            );
            List<Ride> resultList = em.createQuery(criteriaQuery).getResultList();
            return new HashSet<>(resultList);
        } catch (Exception e) {
            throw new EntityException(e, "Não foi possível buscar as caronas!");
        }
    }

    public List<Ride> searchByParameters(String origin, String date) {
        try {
            List<Ride> lista = em.
                    createQuery("SELECT R FROM Ride R JOIN FETCH R.cityDestiny JOIN FETCH R.cityOrigin JOIN FETCH R.cityPassage WHERE R.date = :date AND R.cityPassage.cityName = :city OR R.cityOrigin.cityName = :origin").
                    setParameter("date", date).
                    setParameter("city", origin).
                    setParameter("origin", origin).
                    getResultList();
//            List<Ride> lista = em.createQuery("SELECT R "
//                    + "FROM Ride R LEFT JOIN FETCH R.cityInTheMiddle "
//                    + "WHERE R.cityInTheMiddle.cityNameOrigin = :origin OR R.routeDestiny.cityNameDestination = :destination OR R.date = :date").
//                    setParameter("origin", origin).
//                    setParameter("destination", destination).
//                    setParameter("date", date).
//                    getResultList();
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    public List<Ride> searchRidesByUser(Long id) {
        UserPrincipal user = em.createQuery("SELECT U FROM UserPrincipal U LEFT JOIN FETCH U.rides WHERE U.id = :id", UserPrincipal.class).setParameter("id", id).getSingleResult();
        return user.getRides();
//        List<Ride> lista = em.
//                createQuery("SELECT U FROM UserPrincipal U LEFT JOIN FETCH U.rides WHERE U.id = :id").
//                setParameter("id", id).
//                getResultList();
////            List<Ride> lista = em.createQuery("SELECT R "
////                    + "FROM Ride R LEFT JOIN FETCH R.cityInTheMiddle "
////                    + "WHERE R.cityInTheMiddle.cityNameOrigin = :origin OR R.routeDestiny.cityNameDestination = :destination OR R.date = :date").
////                    setParameter("origin", origin).
////                    setParameter("destination", destination).
////                    setParameter("date", date).
////                    getResultList();
//        return lista;
    }
}
