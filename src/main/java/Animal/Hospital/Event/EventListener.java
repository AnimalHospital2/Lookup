package Animal.Hospital.Event;

import Animal.Hospital.AbstractEvent;
import Animal.Hospital.ReservationStat;
import Animal.Hospital.ReservationStatRepository;
import Animal.Hospital.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EventListener {

    @Autowired
    ReservationStatRepository reservationStatRepository;

    /**
     * 각종 이벤트 발생시 저장을 하는 공간
     * @param message
     */
    @StreamListener(KafkaProcessor.INPUT)
    public void onMessage(@Payload String message) {
        System.out.println(message);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AbstractEvent event = null;
        try {
            event = objectMapper.readValue(message, AbstractEvent.class);

            /**
             * 예약 완료 이벤트
             */
            if( event.getEventType().equals(ReservationReserved.class.getSimpleName())){
                ReservationReserved reservationReserved = objectMapper.readValue(message, ReservationReserved.class);

                ReservationStat reservationStat = new ReservationStat();
                reservationStat.setReservationId(reservationReserved.getId());
                reservationStat.setReservatorName(reservationReserved.getReservatorName());
                reservationStat.setReservationDate(reservationReserved.getReservationDate());
                reservationStat.setPhoneNumber(reservationReserved.getPhoneNumber());
                reservationStat.setReservationStatus("예약 완료");

                reservationStatRepository.save(reservationStat);
            }
            /**
             * 예약 취소 이벤트
             */
            else if( event.getEventType().equals(ReservationCanceled.class.getSimpleName())) {
                ReservationCanceled reservationCanceled = objectMapper.readValue(message, ReservationCanceled.class);

                ReservationStat reservationStat = new ReservationStat();
                reservationStat.setReservationId(reservationCanceled.getId());
                reservationStat.setReservatorName(reservationCanceled.getReservatorName());
                reservationStat.setReservationDate(reservationCanceled.getReservationDate());
                reservationStat.setPhoneNumber(reservationCanceled.getPhoneNumber());
                reservationStat.setReservationStatus("예약 취소");

                reservationStatRepository.save(reservationStat);
            }
            /**
             * 예약 변경 이벤트
             */
            else if( event.getEventType().equals(ReservationChanged.class.getSimpleName())) {
                ReservationChanged reservationChanged = objectMapper.readValue(message, ReservationChanged.class);

                ReservationStat reservationStat = new ReservationStat();
                reservationStat.setReservationId(reservationChanged.getId());
                reservationStat.setReservatorName(reservationChanged.getReservatorName());
                reservationStat.setReservationDate(reservationChanged.getReservationDate());
                reservationStat.setPhoneNumber(reservationChanged.getPhoneNumber());
                reservationStat.setReservationStatus("예약 변경");

                reservationStatRepository.save(reservationStat);
            }
            /**
             * 진료 완료 이벤트
             */
            else if( event.getEventType().equals(Treated.class.getSimpleName())) {
                Treated treated = objectMapper.readValue(message, Treated.class);

                ReservationStat reservationStat = new ReservationStat();
                reservationStat.setReservationId(treated.getReservationId());
                reservationStat.setReservationStatus("진료 완료");

                reservationStatRepository.save(reservationStat);
            }
            /**
             * 수납 완료 이벤트
             */
            else if( event.getEventType().equals(TreatmentFeeAccepted.class.getSimpleName())) {
                TreatmentFeeAccepted treatmentFeeAccepted = objectMapper.readValue(message, TreatmentFeeAccepted.class);

                ReservationStat reservationStat = new ReservationStat();
                reservationStat.setReservationId(treatmentFeeAccepted.getReservationId());
                reservationStat.setFee(treatmentFeeAccepted.getFee());
                reservationStat.setReservationStatus("수납 완료");

                reservationStatRepository.save(reservationStat);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}