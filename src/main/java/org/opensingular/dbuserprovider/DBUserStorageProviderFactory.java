package org.opensingular.dbuserprovider;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.UserStorageProviderFactory;
import org.opensingular.dbuserprovider.persistence.DataSourceProvider;

import java.util.HashMap;
import java.util.Map;

@JBossLog
@AutoService(UserStorageProviderFactory.class)
public class DBUserStorageProviderFactory implements UserStorageProviderFactory<DBUserStorageProvider> {

    private final DataSourceProvider dataSourceProvider = new DataSourceProvider();
    private final Map<String, DataSourceProvider> providerConfigPerInstance = new HashMap<>();
    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void close() {
        dataSourceProvider.close();
    }

    @Override
    public DBUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        DataSourceProvider dataSourceProvider = providerConfigPerInstance.computeIfAbsent(model.getId(), s -> configure(model));
        return new DBUserStorageProvider(session, model, dataSourceProvider);
    }

    private synchronized DataSourceProvider configure(ComponentModel model) {
        log.infov("Creating configuration for model: id={0} name={1}", model.getId(), model.getName());
        dataSourceProvider.configure(model.getName());
        return dataSourceProvider;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel model) throws ComponentValidationException {
        try {
            DataSourceProvider old = providerConfigPerInstance.put(model.getId(), configure(model));
            if (old != null) {
                old.close();
            }
        } catch (Exception e) {
            throw new ComponentValidationException(e.getMessage(), e);
        }
    }

    @Override
    public String getId() {
        return "Postgres-User-Provider";
    }

}
