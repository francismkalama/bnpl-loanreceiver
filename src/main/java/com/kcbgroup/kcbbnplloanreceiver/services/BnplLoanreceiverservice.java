package com.kcbgroup.kcbbnplloanreceiver.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcbgroup.kcbbnplloanreceiver.configs.JMSconfig;
import com.kcbgroup.kcbbnplloanreceiver.models.Bnplrequest;
import com.kcbgroup.kcbbnplloanreceiver.utils.Bnplrepo;
import com.kcbgroup.kcbbnplloanreceiver.utils.RestAckObject;
import com.kcbgroup.kcbbnplloanreceiver.utils.RestResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class BnplLoanreceiverservice {
//    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Bnplrepo bnplrepo;

    @Autowired
    private JMSconfig jmSconfig;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${spring.activemq.incoming-queue}")
    private String bnplqueue;

    @Value("${proc.queue-size}")
    private Integer queueSize;

    @Value("${proc.threads}")
    private Integer threads;


    LinkedBlockingQueue<Bnplrequest> bnplrequestsqueue = new LinkedBlockingQueue<>(10);
    ExecutorService bnplThreads = Executors.newFixedThreadPool(5);

    //------------------initialize queues and threading-------
    @EventListener(ApplicationReadyEvent.class)
    public void initializeBNPLproperties() {
        log.info("--------------------- BNPL Service starting..... -------------");
        log.info("=> initializing queues and threads");
        bnplThreads = Executors.newFixedThreadPool(threads);
        log.info("=> BNPL " + threads + " theads initialized. ");
        bnplrequestsqueue = new LinkedBlockingQueue<>(queueSize);
        log.info("=> BNPL queue of queue size " + queueSize+ " initialized.");

    }

    public ResponseEntity bnplloanReceiver(Bnplrequest bnplrequest, HttpServletRequest req) {
        RestResponse restEntityResponse = new RestResponse(null, HttpStatus.INTERNAL_SERVER_ERROR);
        RestAckObject resobject = new RestAckObject();
        try {
            if (bnplrepo.validateInput(bnplrequest.getMsisdn()) &&bnplrepo.validateInput(bnplrequest.getAmount())) {
                //adds the load request to the lbq stack
                bnplrequestsqueue.put(bnplrequest);
                log.info("==> Loan Request : " + bnplrequest.getRequestId() + " added to LBQ successfully for processing. The LBQ size :"+bnplrequestsqueue.size());
                //provides ack to the received request
                resobject.setStatus("200");
                resobject.setDesciprion("Loan request received successfully for processing");
                return new ResponseEntity<>(resobject, HttpStatus.ACCEPTED);
            } else {
                resobject.setStatus("400");
                resobject.setDesciprion("Missing data provided");
                return restEntityResponse = new RestResponse(resobject, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.info("ERROR Executing BNPL request " + e);
            resobject.setStatus("500");
            resobject.setDesciprion("There was a problem processing BNPL loan request");
            return restEntityResponse = new RestResponse(resobject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Scheduled(fixedDelayString = "60000")
    public void readLBQtoAMQ() {
        if (checkLQBsize() == true){
            log.info("==> Reading record from LBQ to be added to AMQ");
            for (int i = 0; i < threads; i++) {
                bnplThreads.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            insertToBNPLAmq(bnplrequestsqueue.take());
//                        System.out.println("Processing with thread: " + bnplThreads);
                        } catch (Exception e) {
                            log.info("Exception writing to the queue " + e);
                        }
                    }
                });
            }

        }
        log.info("==> The LBQ queue is empty ");
    }

    private void insertToBNPLAmq(Bnplrequest bnplrequest) {
        //inserts the read value from LBQ to AMQ
        try {
            jmsTemplate.convertAndSend(bnplqueue, new ObjectMapper().writer().writeValueAsBytes(bnplrequest));
            log.info("==> Loan request " + bnplrequest.getRequestId() + " added successfuly to " + bnplqueue + " for processing");
        } catch (JsonProcessingException e) {
            log.info("==> Error The was a problem inserting request" + bnplrequest.getRequestId() + " to AMQ for processing" + e);
        }
    }
    private boolean checkLQBsize(){
        Boolean emptyQ = false;
        if (bnplrequestsqueue.size()>0){
            emptyQ = true;
            return emptyQ;
        }
        return emptyQ;
    }
}
