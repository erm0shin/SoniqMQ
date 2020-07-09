package ru.innotechnum.testlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;
import ru.innotechnum.testlistener.dto.Message;
import ru.innotechnum.testlistener.gateway.petstore.api.PetGateway;
import ru.innotechnum.testlistener.gateway.petstore.model.Pet;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class TestListenerApplication {
	private final static Logger LOG = LoggerFactory.getLogger(TestListenerApplication.class);

	private final PetGateway petGateway;
	private final JmsTemplate sonicJmsTemplate;

	@Autowired
	public TestListenerApplication(PetGateway petGateway, JmsTemplate sonicJmsTemplate) {
		this.petGateway = petGateway;
		this.sonicJmsTemplate = sonicJmsTemplate;
	}

	public static void main(String[] args) {
		SpringApplication.run(TestListenerApplication.class, args);
	}

//	@PostConstruct
//	public void test() {
//		petGateway.findPetsByStatus(Pet.StatusEnum.AVAILABLE)
//				.doOnNext(response ->
//						LOG.info(response.get(0).toString())
//				)
//		.subscribe();
//	}

	@PostConstruct
	public void test() {
		final Message message = new Message(1,5);
		sonicJmsTemplate.convertAndSend(
				"test",
				message
		);
	}
}
