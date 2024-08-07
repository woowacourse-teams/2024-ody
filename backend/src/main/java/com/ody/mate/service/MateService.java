package com.ody.mate.service;

import com.ody.common.exception.OdyBadRequestException;
import com.ody.eta.service.EtaService;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.dto.response.MateSaveResponse;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import com.ody.notification.service.NotificationService;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MateService {

    private final MateRepository mateRepository;
    private final EtaService etaService;
    private final NotificationService notificationService;
    private final RouteService routeService;

    public MateSaveResponse saveAndSendNotifications(MateSaveRequest mateSaveRequest, Member member, Meeting meeting) {
        if (mateRepository.existsByMeetingIdAndNickname_Value(meeting.getId(), mateSaveRequest.nickname())) {
            throw new OdyBadRequestException("모임 내 같은 닉네임이 존재합니다.");
        }
        if (mateRepository.existsByMeetingIdAndMemberId(meeting.getId(), member.getId())) {
            throw new OdyBadRequestException("모임에 이미 참여한 회원입니다.");
        }

        Location origin = new Location(
                mateSaveRequest.originAddress(),
                mateSaveRequest.originLatitude(),
                mateSaveRequest.originLongitude()
        );
        RouteTime routeTime = routeService.calculateRouteTime(origin, meeting.getTarget());
        Mate mate = mateRepository.save(mateSaveRequest.toMate(meeting, member, routeTime.getMinutes()));
        etaService.saveFirstEtaOfMate(mate, routeTime);
        notificationService.saveAndSendNotifications(meeting, mate, member.getDeviceToken(), routeTime);
        return MateSaveResponse.from(mate);
    }

    public List<Mate> findAllByMemberAndMeetingId(Member member, long meetingId) {
        if (!mateRepository.existsByMeetingIdAndMemberId(meetingId, member.getId())) {
            throw new OdyBadRequestException("존재하지 않는 모임이거나 약속 참여자가 아닙니다.");
        }
        return mateRepository.findAllByMeetingId(meetingId);
    }
}
