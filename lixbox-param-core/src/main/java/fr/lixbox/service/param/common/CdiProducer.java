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
package fr.lixbox.service.param.common;

import javax.enterprise.inject.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.lixbox.common.util.StringUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.service.param.Constant;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.client.RegistryServiceClient;
import fr.lixbox.service.registry.model.ServiceEntry;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Cette classe produit les objets CDI nécessaire au service
 * 
 * @author ludovic.terral
 */
public class CdiProducer
{
    // ----------- Attribut -----------
    private static final Log LOG = LogFactory.getLog(CdiProducer.class);
    
    private static final String DEFAULT_REGISTRY_URL = "http://localhost:18100/registry/api/1.0";    

    @ConfigProperty(name="registry.uri", defaultValue=DEFAULT_REGISTRY_URL) String registryUri;
    @ConfigProperty(name="redis.uri", defaultValue="") String redisUri;
        

    // ----------- Methode -----------
    @Produces @LocalRegistryConfig
    public RegistryServiceClient getRegistryServiceClient() 
    {
        RegistryServiceClient result = null;
        try
        {
            result = new RegistryServiceClient(registryUri);
        }
        catch(Exception e)
        {
            LOG.error(registryUri);
            LOG.fatal(e);
            //absence de service d'annuaire.
        }
        return result;
    }
    
    
    
    @Produces @LocalRegistryConfig
    public ExtendRedisClient getExtendRedisClient() 
    {
        ExtendRedisClient result = null;
        try
        {
            if (StringUtil.isEmpty(redisUri)||"not_use".equals(redisUri)) 
            {
                ServiceEntry redis = getRegistryServiceClient().discoverService(Constant.REDIS_NAME, Constant.REDIS_VERSION);
                if (redis!=null)
                {
                    redisUri = redis.getPrimary().getUri();
                }
            }
            if (!StringUtil.isEmpty(redisUri))
            {
                String hostName = redisUri.substring(6,redisUri.lastIndexOf(':'));
                int port = Integer.parseInt(redisUri.substring(redisUri.lastIndexOf(':')+1));
                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMaxTotal(40);
                poolConfig.setTestOnBorrow(true);
                poolConfig.setTestOnReturn(true);
                poolConfig.setMaxIdle(40);
                poolConfig.setMinIdle(1);
                poolConfig.setTestWhileIdle(true);
                poolConfig.setNumTestsPerEvictionRun(10);
                poolConfig.setTimeBetweenEvictionRunsMillis(3000L);
                poolConfig.setBlockWhenExhausted(false);
                poolConfig.setTestWhileIdle(true);
                JedisPool pool = new JedisPool(poolConfig, hostName, port);
                result = new ExtendRedisClient(pool);
            }
        }
        catch (Exception e)
        {
            LOG.fatal(e.getMessage());
        }
        return result;
    }
}