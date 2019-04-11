package com.isc.astd.service;

import com.isc.astd.domain.File;
import com.isc.astd.domain.FilePosition;
import com.isc.astd.domain.Position;
import com.isc.astd.domain.Route;
import com.isc.astd.domain.RoutePosition;
import com.isc.astd.service.dto.EcpPersonDTO;
import com.isc.astd.service.dto.PositionDTO;
import com.isc.astd.service.dto.SignedPositionDTO;
import com.isc.astd.service.mapper.Mapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author p.dzeviarylin
 */
@Service
public class FileEcpService {

	private final int POSITION_ID = 5;
	private final int ROUTE_ID = 5;

    private final Mapper mapper;

    private final UserService userService;

    public FileEcpService(Mapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    SignedPositionDTO getNextSignPosition(File file) {
        List<SignedPositionDTO> signedPosition = getSignedPositions(file);
        List<SignedPositionDTO> signPosition = getSignPositions(file.getRoute());

        if(signedPosition.size() == 0){
            return  signPosition.stream().
                    min(Comparator.comparingInt(SignedPositionDTO::getOrder)).orElse(null);
        } else if(signPosition.size() > signedPosition.size()){
            return  signPosition.stream().
                    filter(routePosition -> !signedPosition.contains(routePosition)).
                    min(Comparator.comparingInt(SignedPositionDTO::getOrder)).orElse(null);
        } else {
            return null;
        }
    }

    private List<SignedPositionDTO> getSignPositions(Route route) {
        return route.getRoutePositions().stream().
                map(routePosition -> new SignedPositionDTO(routePosition.getPosition(), routePosition.getId().getOrder())).
                collect(Collectors.toList());
    }

    List<SignedPositionDTO> getSignedPositions(File file) {
        return file.getFilePositions().stream().
                filter(filePosition -> filePosition.getEcp() != null).
                map(filePosition -> new SignedPositionDTO(filePosition.getId().getPosition(), filePosition.getId().getOrder(), filePosition.getCreatedBy())).
                collect(Collectors.toList());
    }

    public SignedPositionDTO getRejectedPosition(File file) {
        return file.getFilePositions().stream().
                filter(filePosition -> filePosition.getEcp() == null).
                findFirst().
                map(filePosition -> new SignedPositionDTO(filePosition.getId().getPosition(), filePosition.getId().getOrder(), filePosition.getCreatedBy())).
                orElseThrow(() -> new RuntimeException("Rejected Position not found"));
    }

    public int getRoutePositionAnyOrder(File file, Position position) {
        return file.getRoute().getRoutePositions().stream().filter(routePosition -> routePosition.getPosition().equals(position)).findAny().get().getId().getOrder();
    }

    int getTotalSignsNum(File file) {
        return Math.toIntExact(file.getRoute().getRoutePositions().stream().map(RoutePosition::getGroup).distinct().count());
    }

    int getSignedNum(File file) {
        return Math.toIntExact(file.getFilePositions().stream().filter(docPosition -> docPosition.getEcp() != null).count());
    }

    List<EcpPersonDTO> getEcpPersons(File doc) {
        final Map<Integer, List<RoutePosition>> routePositionsByGroup = doc.getRoute().getRoutePositions().stream().collect(Collectors.groupingBy(RoutePosition::getGroup));
        final List<EcpPersonDTO> ecpPersons = routePositionsByGroup.entrySet().stream().
                map(routePositionByGroup -> new EcpPersonDTO(
                        doc.getId(),
                        mapper.mapAsList(routePositionByGroup.getValue().stream().map(RoutePosition::getPosition).collect(Collectors.toList()), PositionDTO.class),
                        routePositionByGroup.getValue().stream().map(routePosition -> routePosition.getPosition().getName()).collect(Collectors.joining(", ")),
                        routePositionByGroup.getValue().stream().map(routePosition -> routePosition.getStatus().getText()).collect(Collectors.joining(", ")),
                        routePositionByGroup.getValue().stream().map(routePosition -> routePosition.getId().getOrder()).findAny().get()
                )).
                sorted(Comparator.comparingInt(EcpPersonDTO::getRoutePositionOrder)).collect(Collectors.toList());

        doc.getFilePositions().stream().
//                filter(filePosition -> filePosition.getEcp() != null).
        forEach(filePosition -> ecpPersons.stream().
                filter(ecpPersonDTO ->
                        ecpPersonDTO.getPositions().stream().anyMatch(positionDTO -> positionDTO.getId() == filePosition.getId().getPosition().getId()) &&
                                ecpPersonDTO.getRoutePositionOrder() == filePosition.getId().getOrder()
                ).
                findAny().
                ifPresent(ecpPersonDTO -> {
                    ecpPersonDTO.setName(userService.getUser(filePosition.getCreatedBy()).getName());
                    ecpPersonDTO.setCreatedDate(filePosition.getCreatedDate());
                    ecpPersonDTO.setPosition(filePosition.getId().getPosition().getName());
                    if(filePosition.getMsg() != null) {
                        ecpPersonDTO.setMsg(filePosition.getMsg());
                        ecpPersonDTO.setRoutePositionStatus(File.Status.REJECTED.getText());
                    }
                    else {
                        ecpPersonDTO.setInvalid(filePosition.isInvalid());
                    }
                })
        );

        return ecpPersons;
    }

    boolean isSingable(File file){
      return file.getBranchType() == File.BranchType.DEFAULT && (file.getStatus() == File.Status.DEFAULT || file.getStatus() == File.Status.SIGNING);
    }

    boolean isCanBeSigned(Position myPosition, File file) {
        return isSingable(file) && file.getRoute().getRoutePositions().stream().map(RoutePosition::getPosition).anyMatch(position -> position.equals(myPosition));
    }

    boolean isNotSignedYet(User user, Position myPosition, File file, boolean canBeSigned) {
        /*return canBeSigned &&
                getSignedPositions(file).stream().
                        noneMatch(
                                signedPosition -> signedPosition.getPosition().equals(myPosition) &&
                                        signedPosition.getCreatedBy().equals(user.getUsername())
                        );*/
        return true;
    }

    boolean isMyOrderToSign(Position myPosition, boolean isNotSignedYet, SignedPositionDTO nextSignPosition, File file) {
    	boolean isMyOrderToSign = isMyOrderToSign(myPosition, isNotSignedYet, nextSignPosition);
    	if(isMyOrderToSign && myPosition.getId() == POSITION_ID && file.getRoute().getId() == ROUTE_ID) {
		    final FilePosition prevFilePosition = file.getFilePositions().stream().filter(filePosition -> filePosition.getEcp() != null).max(Comparator.comparingInt(value -> value.getId().getOrder())).orElse(null);
		    if(prevFilePosition != null && prevFilePosition.getId().getPosition().getId().equals(myPosition.getId())) {
			   if(StringUtils.isBlank(file.getFioSign1()) || StringUtils.isBlank(file.getFioSign2())){
				   isMyOrderToSign = false;
			   }
		    }
	    }
        return isMyOrderToSign;
    }

	boolean isMyOrderToSign(Position myPosition, boolean isNotSignedYet, SignedPositionDTO nextSignPosition) {
		return isNotSignedYet && nextSignPosition != null && myPosition.equals(nextSignPosition.getPosition());
	}

    boolean isMyOrderToSign(File file, User user) {
        Position myPosition = userService.getUser(user.getUsername()).getPosition();
        boolean canBeSigned = isCanBeSigned(myPosition, file);
        boolean isNotSignedYet = isNotSignedYet(user, myPosition, file, canBeSigned);
        SignedPositionDTO nextSignPosition = getNextSignPosition(file);
        return isMyOrderToSign(myPosition, isNotSignedYet, nextSignPosition/*, file*/);
    }

    boolean isSignedBy(Position position, File file) {
        return file.getFilePositions().stream().anyMatch(filePosition -> filePosition.getEcp() != null && filePosition.getId().getPosition().equals(position));
    }

    FilePosition getSignatureFor(Position position, File file) {
        return file.getFilePositions().stream().filter(filePosition -> filePosition.getEcp() != null && filePosition.getId().getPosition().equals(position)).findFirst().orElse(null);
    }

    boolean isOriginalCheckedSigned(Position myPosition, File file){
    	boolean signed = file.getRoute().getId() == ROUTE_ID && myPosition.getId() == POSITION_ID && StringUtils.isNotBlank(file.getFioSign1()) && StringUtils.isNotBlank(file.getFioSign2());
    	if(signed) {
		    signed = false;
		    final List<FilePosition> filePositions = file.getFilePositions().stream().filter(filePosition -> filePosition.getEcp() != null && filePosition.getId().getPosition().getId() == POSITION_ID).collect(Collectors.toList());
		    for (int i = 0; i < filePositions.size(); i++) {
			    if(i > 0){
				    FilePosition filePosition = filePositions.get(i);
				    if(filePosition.getId().getOrder() - filePositions.get(i - 1).getId().getOrder() == 1){// signatures are podriad
					    signed = true;
					    break;
				    }
			    }
		    }
	    }
	    return signed;
    }

    /*void setMyMoreSigns(List<MoreSignsDTO> dtos, FileBaseDTO fileDTO) {
        fileDTO.setMoreSigns(dtos);
        fileDTO.setMySignsNum(Math.toIntExact(fileDTO.getMoreSigns().stream().mapToLong(MoreSignsDTO::getSignNum).count()));
    }*/
}
