package ui.entities.config.service;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ui.entities.config.Configuration;

public class ConfigService {
	private EntityManager em;

	public ConfigService(EntityManager em) {
		this.em = em;
	};

	public Configuration create(Configuration oldConfiguration) {
		if (oldConfiguration != null) {
			remove(oldConfiguration);
		}

		Configuration config = new Configuration();
		config.getSidplay2Section().setVersion(
				Configuration.REQUIRED_CONFIG_VERSION);
		em.persist(config);
		flush();
		return config;
	}

	public Configuration getOrCreate() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Configuration> q = cb.createQuery(Configuration.class);
		Root<Configuration> h = q.from(Configuration.class);
		q.select(h);
		List<Configuration> resultList = em.createQuery(q).setMaxResults(1)
				.getResultList();
		if (resultList.size() != 0) {
			return resultList.get(0);
		}
		return create(null);
	}

	public void exportCfg(Configuration config, File file) {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(Configuration.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(config, file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public Configuration importCfg(File file) {
		if (file.exists()) {
			try {
				JAXBContext jaxbContext = JAXBContext
						.newInstance(Configuration.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				Object obj = unmarshaller.unmarshal(file);
				if (obj instanceof Configuration) {
					Configuration detachedConfig = (Configuration) obj;

					Configuration mergedConfig = em.merge(detachedConfig);
					em.persist(mergedConfig);
					flush();

					return mergedConfig;
				}
			} catch (JAXBException e) {
				System.err.println(e.getMessage());
			}
		}
		return create(null);
	}

	private void remove(Configuration config) {
		em.remove(config);
		flush();
		em.clear();
	}

	public void commit(Configuration config) {
		em.getTransaction().begin();
		try {
			em.persist(config);
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}
	}

	private void flush() {
		em.getTransaction().begin();
		try {
			em.flush();
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}
	}

}
