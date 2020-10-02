/*******************************************************************************
 *    
 *                           FRAMEWORK Lixbox
 *                          ==================
 *      
 *    This file is part of lixbox-service.
 *
 *    lixbox-param is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    lixbox-param is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *    along with lixbox-param.  If not, see <https://www.gnu.org/licenses/>
 *   
 *   @AUTHOR Lixbox-team
 *
 ******************************************************************************/
package fr.lixbox.service.param.redis;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.resource.LixboxResources;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.orm.redis.query.RedisSearchQueryHelper;
import fr.lixbox.service.param.ParametreService;
import fr.lixbox.service.param.model.Parametre;
import fr.lixbox.service.registry.RegistryService;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.model.health.Check;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;
import io.redisearch.Query;

/**
 * Ce service de parametres fonctionne sur Redis.
 * 
 * @author ludovic.terral
 */
@ApplicationScoped
@Path(ParametreService.SERVICE_URI)
@Produces({"application/json"})
@Consumes({"application/json"})
public class ParametreServiceBean implements ParametreService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 201705120951L;
    private static final Log LOG = LogFactory.getLog(ParametreService.class);

    private static final String SERVICE_REDIS_TEXT = "LE SERVICE REDIS ";
    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";


    @Inject @LocalRegistryConfig RegistryService registryService;
    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;



    // ----------- Methode -----------
    @Override
    @PermitAll
    public ServiceState checkHealth() 
    {
        return checkReady();
    }

    
    
    @Override
    @PermitAll
    public ServiceState checkReady()
    {
        LOG.debug("Check Health started");
        ServiceState state = new ServiceState();
        
                
        //controle de redis
        try
        {
            redisClient.getRedisClient().ping();
            state.setStatus(ServiceStatus.UP);
            LOG.debug(SERVICE_REDIS_TEXT+" EST DISPONIBLE");
        }
        catch (Exception e)
        {
            LOG.fatal(e,e);
            LOG.error(SERVICE_REDIS_TEXT+" N'EST PAS DISPONIBLE");
            state.setStatus(ServiceStatus.DOWN);
            state.getChecks().add(new Check(ServiceStatus.DOWN, SERVICE_REDIS_TEXT+" N'EST PAS DISPONIBLE"));
        }
        return state;
    }
    
    

    @Override 
    @PermitAll
    public ServiceState checkLive() 
    {
        return new ServiceState(ServiceStatus.UP);
    }



    /**
     * Cette methode renvoie la version courante du code. 
     */
    @Override
    @PermitAll
    public String getVersion()
    {   
        return CodeVersionUtil.getVersion(this.getClass());
    }

    
    
    @Override
    public Parametre getParametreById(String id) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(id))
        {
            throw new BusinessException(LixboxResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { ParametreService.SERVICE_CODE, "id" }));   
        }
        
        Parametre paramGps=null;
        try
        {
            paramGps = redisClient.findById(Parametre.class, id);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return paramGps;
    }



    @Override
    public Parametre synchronize(Parametre param) throws BusinessException
    {
        try
        {
            param.validate();
            param = redisClient.merge(param);
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return param;
    }



    @Override
    public boolean remove(String oid) throws BusinessException
    {
        boolean result = false;
        try
        {
            redisClient.remove(Parametre.class, oid);
            result = true;
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return result;
    }



    @Override
    public List<Parametre> getParametres() throws BusinessException
    {
        List<Parametre> params=new ArrayList<>();
        try
        {
            Query query = new Query("*");
            query.limit(0, 10000);
            query.setSortBy("code", true);
            params.addAll(redisClient.findByExpression(Parametre.class, query));
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return params;
    }



    @Override
    public List<Parametre> getParametresByService(String serviceId) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(serviceId))
        {
            throw new BusinessException(LixboxResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { ParametreService.SERVICE_CODE, "serviceId" }));   
        }
        List<Parametre> params=new ArrayList<>();
        try
        {
            Query query = new Query(RedisSearchQueryHelper.toStringAttribute("service",serviceId));
            query.limit(0, 500);
            query.setSortBy("code", true);
            params.addAll(redisClient.findByExpression(Parametre.class, query));
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return params;
    }



    @Override
    public Parametre getParametreByCode(String serviceId, String code) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(serviceId))
        {
            throw new BusinessException(LixboxResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { ParametreService.SERVICE_CODE, "serviceId" }));   
        }
        if (StringUtil.isEmpty(code))
        {
            throw new BusinessException(LixboxResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { ParametreService.SERVICE_CODE, "code" }));   
        }
        
        Parametre parametre=null;
        try
        {
            List<Parametre> parametres = new ArrayList<>();
            Query query = new Query(RedisSearchQueryHelper.toStringAttribute("code",code)+" "+
                    RedisSearchQueryHelper.toStringAttribute("service",serviceId));
            query.limit(0, 500);
            query.setSortBy("code", true);
            parametres.addAll(redisClient.findByExpression(Parametre.class, query));
            if (parametres.size()==1)
            {
                parametre=parametres.get(0);
            }
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return parametre;
    }



    @Override
    public <T> T getValueByCode(String serviceId, String code, String defaultValue, String defaultValueClass)
            throws BusinessException
    {
        T value=null;
        try
        {
            Parametre param = getParametreByCode(serviceId, code);
            if (param!=null && StringUtil.isNotEmpty(param.getValue()) && StringUtil.isNotEmpty(param.getValueClass()))
            {
                value = extractTypedValue(param.getValueClass(), param.getValue());
            }
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, !(StringUtil.isNotEmpty(defaultValue)&&StringUtil.isNotEmpty(defaultValueClass)));
        }
        if (value==null)
        {
            value = extractTypedValue(defaultValueClass, defaultValue);
        }
        return value;
    }

    
    
    @SuppressWarnings("unchecked")
    private <T> T extractTypedValue(String classz, String json) throws BusinessException
    {
        T value=null;
        try
        {
            if (classz.contains("String"))
            {
                value=(T) json;
            }
            else
            {
                final Class<?> typed = Class.forName(classz);
                TypeReference<?> typeRef= new TypeReference<Object>()
                {
                    @Override
                    public Type getType() {
                        return typed;
                    }
                };
                value = (T) JsonUtil.transformJsonToObject(json, typeRef);
            }
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
        }
        return value;
    }
}