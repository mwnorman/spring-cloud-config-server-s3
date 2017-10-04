package org.springframework.cloud.config.s3.yaml;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class YamlFileConfiguration extends AbstractFileConfiguration {
	public YamlFileConfiguration() {
	}

	public YamlFileConfiguration(File file) throws ConfigurationException {
		super(file);
	}

	public YamlFileConfiguration(String url) throws ConfigurationException {
		super(url);
	}

	public YamlFileConfiguration(URL url) throws ConfigurationException {
		super(url);
	}

	public void load(Reader in) throws ConfigurationException {
		Yaml yaml = new Yaml();
		Iterable<Object> it_conf = yaml.loadAll(in);
		Iterator i$ = it_conf.iterator();

		while(i$.hasNext()) {
			Object obj = i$.next();
			if(obj instanceof Map) {
				Map<String, Map<String, Object>> configuration = (Map)obj;
				this.getKeyValue(configuration, "");
			}
		}
	}

	public void save(Writer out) throws ConfigurationException {
	}

	private void getKeyValue(Map<String, Map<String, Object>> map, String key) {
		String localKey = key;

		for(Iterator i$ = map.keySet().iterator(); i$.hasNext(); key = localKey) {
			String configKey = (String)i$.next();
			Object configValue = map.get(configKey);
			if(configValue instanceof Map) {
				key = key + configKey;
				key = key + ".";
				this.getKeyValue((Map)configValue, key);
			} else {
				key = key + configKey;
				this.addProperty(key, configValue.toString());
			}
		}

	}

	public Map<String, String> getProperties() {
		Map<String, String> properties = new HashMap<>();
		Iterator<String> it = this.getKeys();
		while (it.hasNext()) {
			String key = it.next();
			properties.put(key, this.getProperty(key).toString());
		}
		return properties;
	}
}
