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
package fr.lixbox.service.param.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.orm.entity.model.AbstractValidatedEntity;
import fr.lixbox.orm.entity.model.Dao;
import fr.lixbox.orm.redis.model.RedisSearchDao;
import fr.lixbox.service.param.Constant;
import io.redisearch.Schema;

/**
 * Cette entite correspond aux informations d'un paramètre
 * d'une application.
 *  
 * @author ludovic.terral
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Parametre extends AbstractValidatedEntity implements Dao, RedisSearchDao
{
    // ----------- Attribut -----------    
	private static final long serialVersionUID = 202006131456L;
	
	private String oid;
	private String code;
	private String libelle;
	private String value;
	private String valueClass;
	private String serviceId;



	// ----------- Methode -----------
    public String getOid()
    {
        return oid;
    }
    public void setOid(String oid)
    {
        this.oid = oid;
    }
    
    
    
    public String getCode()
    {
        return code;
    }
    public void setCode(String code)
    {
        this.code = code;
    }
    
    
    
    public String getLibelle()
    {
        return libelle;
    }
    public void setLibelle(String libelle)
    {
        this.libelle = libelle;
    }
    
    
    
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    
    
    
    public String getValueClass()
    {
        return valueClass;
    }
    public void setValueClass(String valueClass)
    {
        this.valueClass = valueClass;
    }
    
    
    
    public String getServiceId()
    {
        return serviceId;
    }
    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    
    
    @Override
    public String toString()
    {
        return JsonUtil.transformObjectToJson(this, false);
    }
    
    
    
    public static Parametre valueOf(String json)
    {
        return JsonUtil.transformJsonToObject(json, new TypeReference<Parametre>() {});
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public String getKey()
    {
        return Constant.APP_CACHE_CODE+":OBJECT:"+Parametre.class.getCanonicalName()+":"+oid;
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public Schema getIndexSchema() 
    {
        return new Schema()
                .addSortableTextField("code", 2)
                .addSortableTextField("service", 2);
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public Map<String, Object> getIndexFieldValues()
    {
        Map<String, Object> indexFields = new HashMap<>();
        indexFields.put("code", code);
        indexFields.put("service", serviceId);
        return indexFields;
    }

    

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public long getTTL()
    {
        return 0;
    }
}