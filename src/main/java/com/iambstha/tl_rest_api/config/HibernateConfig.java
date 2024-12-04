package com.iambstha.tl_rest_api.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HibernateConfig implements HibernatePropertiesCustomizer {

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("org.hibernate.envers.audit_table_suffix", "_log");
        hibernateProperties.put("org.hibernate.envers.revision_type_field_name", "revision_type");
        hibernateProperties.put("org.hibernate.envers.revision_field_name", "revision_id");
        hibernateProperties.put("org.hibernate.envers.store_data_at_delete", true);
    }
}
