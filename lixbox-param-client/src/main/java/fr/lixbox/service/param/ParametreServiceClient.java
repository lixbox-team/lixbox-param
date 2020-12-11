/*******************************************************************************
 *    
 *                           FRAMEWORK Lixbox
 *                          ==================
 *      
 *    This file is part of lixbox-param.
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
package fr.lixbox.service.param;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.resource.LixboxResources;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.service.common.client.MicroServiceClient;
import fr.lixbox.service.param.model.Parametre;

/**
 * Cette classe est le client d'accès au service parametre.
 * 
 * @author ludovic.terral
 *
 */
public class ParametreServiceClient extends MicroServiceClient implements ParametreService
{
    // ----------- Attribut(s) -----------
    private static final long serialVersionUID = -7504335790007649401L;
    private static final Log LOG = LogFactory.getLog(ParametreServiceClient.class);
    private static final String MSG_ERROR_EXCEPUTI_02_KEY = "MSG.ERROR.EXCEPUTI_02";
    private static final String MSG_ERROR_EXCEPUTI_09_KEY = "MSG.ERROR.EXCEPUTI_09";
    
    
    
    // ----------- Methode(s) -----------
    public ParametreServiceClient()
    {
        init();
    }
    public ParametreServiceClient(String serviceRegistryUri)
    {
        init(serviceRegistryUri);
    }
    @Override
    protected void loadInfosService()
    {
        serviceName = ParametreService.SERVICE_NAME;
        serviceVersion = ParametreService.SERVICE_VERSION;
    } 
    @Override
    protected void syncCache()
    {
        //sans objet pas de cache pour ce service
    }
    @Override
    protected void initStore()
    {
        //sans objet pas de cache pour ce service
    }
    @Override
    protected void loadCache()
    {
        //sans objet pas de cache pour ce service
    }
    
    
    
    @Override
    public List<Parametre> getParametres() throws BusinessException
    {
        List<Parametre> result=null;
        WebTarget service = getService();
        if(service!=null)
        {   
            Response response = service
                    .path("/params")
                    .request().get();
            result = parseResponse(response,  new GenericType<List<Parametre>>(){});
        }
        else
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09_KEY, new String[]{ParametreService.SERVICE_CODE, ParametreService.SERVICE_NAME}));
        }
        return result;
    }
    
    
    
    @Override
    public Parametre getParametreById(String id) throws BusinessException
    {
        if (StringUtil.isEmpty(id))
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02_KEY, new String[] { ParametreService.SERVICE_CODE, "id"}));
        }
        
        Parametre result=null;
        WebTarget service = getService();
        if(service!=null)
        {   
            Response response = service
                    .path("/param")
                    .path(id)
                    .request().get();
            result = parseResponse(response,  new GenericType<Parametre>(){});
        }
        else
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09_KEY, new String[]{ParametreService.SERVICE_CODE, ParametreService.SERVICE_NAME}));
        }
        return result;
    }
    
    
    
    @Override
    public Parametre synchronize(Parametre param) throws BusinessException
    {
        if (param==null)
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02_KEY, new String[] { ParametreService.SERVICE_CODE, "param"}));
        }
        
        Parametre result=null;
        WebTarget service = getService();
        if(service!=null)
        {   
            Response response = service
                    .path("/param/sync")
                    .request().post(Entity.json(param.toString()));
            result = parseResponse(response,  new GenericType<Parametre>(){});
        }
        else
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09_KEY, new String[]{ParametreService.SERVICE_CODE, ParametreService.SERVICE_NAME}));
        }
        return result;
    }
    
    
    
    @Override
    public boolean remove(String id) throws BusinessException
    {
        if (StringUtil.isEmpty(id))
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02_KEY, new String[] { ParametreService.SERVICE_CODE, "id"}));
        }
        
        boolean result=false;
        WebTarget service = getService();
        if(service!=null)
        {   
            Response response = service
                    .path("/param")
                    .path(id)
                    .request().delete();
            result = parseResponse(response,  new GenericType<Boolean>(){});
        }
        else
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09_KEY, new String[]{ParametreService.SERVICE_CODE, ParametreService.SERVICE_NAME}));
        }
        return result;
    }
    
    
    
    @Override
    public List<Parametre> getParametresByService(String serviceId) throws BusinessException
    {
        if (StringUtil.isEmpty(serviceId))
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02_KEY, new String[] { ParametreService.SERVICE_CODE, "serviceId"}));
        }
        
        List<Parametre> result=null;
        WebTarget service = getService();
        if(service!=null)
        {   
            Response response = service
                    .path(serviceId)
                    .path("params")
                    .request().get();
            result = parseResponse(response,  new GenericType<List<Parametre>>(){});
        }
        else
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09_KEY, new String[]{ParametreService.SERVICE_CODE, ParametreService.SERVICE_NAME}));
        }
        return result;
    }
    
    
    
    @Override
    public Parametre getParametreByCode(String serviceId, String code) throws BusinessException
    {
        if (StringUtil.isEmpty(serviceId))
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02_KEY, new String[] { ParametreService.SERVICE_CODE, "serviceId"}));
        }
        if (StringUtil.isEmpty(code))
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02_KEY, new String[] { ParametreService.SERVICE_CODE, "code"}));
        }
        
        Parametre result=null;
        WebTarget service = getService();
        if(service!=null)
        {   
            Response response = service
                    .path(serviceId)
                    .path("/param")
                    .path(code)
                    .request().get();
            result = parseResponse(response,  new GenericType<Parametre>(){});
        }
        else
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09_KEY, new String[]{ParametreService.SERVICE_CODE, ParametreService.SERVICE_NAME}));
        }
        return result;
    }
    
    
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValueByCode(String serviceId, String code, String defaultValue, String defaultValueClass)
        throws BusinessException
    {
        if (StringUtil.isEmpty(serviceId))
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02_KEY, new String[] { ParametreService.SERVICE_CODE, "serviceId"}));
        }
        if (StringUtil.isEmpty(code))
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02_KEY, new String[] { ParametreService.SERVICE_CODE, "code"}));
        }
        if (StringUtil.isEmpty(defaultValue))
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02_KEY, new String[] { ParametreService.SERVICE_CODE, "defaultValue"}));
        }
        if (StringUtil.isEmpty(defaultValueClass))
        {
            throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_02_KEY, new String[] { ParametreService.SERVICE_CODE, "defaultValueClass"}));
        }
        
        T result=null;
        WebTarget service = getService();
        if(service!=null)
        {   
            Response response=null;
            try
            {
                response = service
                        .path(serviceId)
                        .path("value")
                        .path(code)
                        .queryParam("default", URLEncoder.encode(defaultValue, StandardCharsets.UTF_8.toString()))
                        .queryParam("defaultClass",defaultValueClass)
                        .request().get();
            }
            catch (UnsupportedEncodingException e)
            {
                ExceptionUtil.traiterException(e, ParametreService.SERVICE_CODE, true);
            }
            
            result = parseResponse(response,  new TypeReference<T>()
            {
                @Override
                public Type getType() {
                    try
                    {
                        return Class.forName(defaultValueClass);
                    }
                    catch (ClassNotFoundException e)
                    {
                        LOG.fatal(e);
                    }
                    return null;
                }
            });
        }
        else
        {
            try
            {
                result = ((T) JsonUtil.transformJsonToObject(defaultValue, new TypeReference<Object>(){
                    @Override
                    public Type getType()
                    {
                        Type classz = String.class;
                        try
                        {
                            classz = Class.forName(defaultValueClass);
                        }
                        catch (ClassNotFoundException e)
                        {
                            LOG.error(e,e);
                        }
                        return classz;
                    }
                }));
            }
            catch(Exception e)
            {
                throw new BusinessException(LixboxResources.getString(MSG_ERROR_EXCEPUTI_09_KEY, new String[]{ParametreService.SERVICE_CODE, ParametreService.SERVICE_NAME}));
            }
        }
        return result;
    }
}