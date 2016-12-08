package com.chattypie.datasource;

import static java.lang.String.format;

import java.io.IOException;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.DisposableBean;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import com.xebialabs.overcast.host.DockerHost;

/**
 * A factory class for creating a {@link DataSource} instance and its underlying database
 * (running in a Docker container).
 * <p>
 * Be aware that:
 * * The class has a dependency on docker (both the cli and the docker engine must be installed locally)
 * * Failing to call shutdown after the application is finished would result in the database container not
 * being stopped/removed, which is a serious resource leak.
 * <p>
 * NB: It is important to implement {@link DisposableBean} here in order to properly clean up the test db container
 */
@Slf4j
public class LocalDatasourceFactory implements DisposableBean {
	private static final int MYSQL_DATABASE_PORT = 3306;
	private final CloudHost mysqlHost;

	public LocalDatasourceFactory() throws IOException {
		log.info("Starting a local db instance");
		mysqlHost = CloudHostFactory.getCloudHost("mysql");
		mysqlHost.setup();
		String containerId = ((DockerHost) mysqlHost).getDockerDriver().getContainerId();
		try {
			new ProcessBuilder(
				"docker", "run",
				"--link", format("%s:mycontainer", containerId),
				"--rm", "martin/wait"
			).start().waitFor();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public DataSource getDatasource() {
		String url = format(
			"jdbc:mysql://%s:%s/chatty_pie_connector?createDatabaseIfNotExist=true&useLegacyDatetimeCode=false",
			mysqlHost.getHostName(),
			mysqlHost.getPort(MYSQL_DATABASE_PORT)
		);

		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setURL(url);
		mysqlDataSource.setUser("root");
		mysqlDataSource.setPassword("password");
		return mysqlDataSource;
	}

	@Override
	public void destroy() throws Exception {
		//NB! : Very important to have the removeLocalDbInstance method here: when we are finished using a container,
		//		we should remove it to avoid resource leaks
		mysqlHost.teardown();
	}
}