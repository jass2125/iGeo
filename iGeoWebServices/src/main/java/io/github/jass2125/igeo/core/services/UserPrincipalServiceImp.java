/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jass2125.igeo.core.services;

import io.github.jass2125.igeo.core.dao.UserPrincipalDao;
import io.github.jass2125.igeo.core.entity.Count;
import io.github.jass2125.igeo.core.entity.Ride;
import io.github.jass2125.igeo.core.entity.UserPrincipal;
import io.github.jass2125.igeo.core.entity.enums.Status;
import io.github.jass2125.igeo.core.exceptions.ApplicationException;
import io.github.jass2125.igeo.core.exceptions.CryptographyException;
import io.github.jass2125.igeo.core.exceptions.EncodingException;
import io.github.jass2125.igeo.core.exceptions.EntityException;
import io.github.jass2125.igeo.core.services.client.UserPrincipalService;
import io.github.jass2125.igeo.core.util.PasswordEncriptor;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 *
 * @author <a href="mailto:jair_anderson_bs@hotmail.com">Anderson Souza</a>
 * @since Jun 24, 2017 2:20:27 PM
 */
@Stateless
@Remote(UserPrincipalService.class)
public class UserPrincipalServiceImp implements UserPrincipalService {

    @EJB
    private UserPrincipalDao dao;
    @EJB
    private PasswordEncriptor encriptor;

    public UserPrincipalServiceImp() {
    }

    @Override
    public UserPrincipal login(Count count) throws ApplicationException {
        try {
            String encryptPassword = encriptor.encryptPassword(count.getPassword());
            UserPrincipal login = dao.login(count.getEmail(), encryptPassword);
            return login;
        } catch (EncodingException | CryptographyException | EntityException e) {
            throw new ApplicationException(e, e.getMessage());
        }
    }

    @Override
    public UserPrincipal register(UserPrincipal userPrincipal) throws ApplicationException {
        try {
            Count count = dao.searchUserPrincipalByEmail(userPrincipal.getCount().getEmail());
            if (count != null) {
                throw new ApplicationException("Email já cadastrado, tente outro");
            }
            String passwordEncrypted = encriptor.encryptPassword(userPrincipal.getCount().getPassword());
            userPrincipal.getCount().setPassword(passwordEncrypted);
            userPrincipal.setProfileStatus(Status.ACTIVE);
            return dao.save(userPrincipal);
        } catch (EncodingException | CryptographyException | EntityException e) {
            throw new ApplicationException(e, e.getMessage());
        }
    }

    @Override
    public UserPrincipal delete(UserPrincipal userPrincipal) throws ApplicationException {
        try {
            Count count = dao.searchUserPrincipalByEmail(userPrincipal.getCount().getEmail());
            String passwordEncrypted = encriptor.encryptPassword(userPrincipal.getCount().getPassword());
            userPrincipal.getCount().setPassword(passwordEncrypted);
//            return dao.save(userPrincipal);
            return null;
        } catch (Exception e) {
            throw new ApplicationException(e, e.getMessage());
        }
    }

    @Override
    public UserPrincipal update(UserPrincipal userPrincipal) throws ApplicationException {
        try {
            return dao.update(userPrincipal);
        } catch (EntityException e) {
            throw new ApplicationException(e, e.getMessage());
        }
    }

    @Override
    public UserPrincipal searchUserPrincipalById(Long id) throws ApplicationException {
        try {
            return dao.searchById(id);
        } catch (EntityException e) {
            throw new ApplicationException(e, e.getMessage());
        }
    }

    @Override
    public Ride addRide(Long id, Ride ride) throws ApplicationException {
        try {
            UserPrincipal userPrincipal = searchUserPrincipalById(id);
            if (userPrincipal != null) {
                userPrincipal.addRide(ride);
                dao.update(userPrincipal);
            }
            return ride;
        } catch (ApplicationException | EntityException e) {
            throw new ApplicationException(e, e.getMessage());
        }
    }
}
