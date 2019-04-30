package com.isc.astd.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.isc.astd.domain.File;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
public class FileBaseDTO {

    private Long id;
    private Long docId;
    private Long routeId;
    private Long nextSignPositionId;
    private String routeName;
    private Long routePositionId;
    private File.Status status;
    private File.BranchType branchType;
    private String name;
    private String listNum;
    private boolean paperShL;
    private boolean paperShChTD;
    private String descr;
    private String noteShl;
    private String themeShchtd;
    private String contentType;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    private Instant createdDate;
    private long size;
    private int signedNum = 0;
//    private int mySignsNum = 0;
    private int totalSignsNum = 0;
    private boolean canBeSigned = false;
    private boolean notSignedYet = true;
    private boolean myOrderToSign = false;
    private boolean myOrderToSignAfterUpdate = false;
    private boolean originalCheckedSigned = false;
    private boolean hasPrevVersion = false;
    private Long prevVersionId;
    private boolean hasNextVersion = false;
    private Long nextVersionId;
    private String routePositionStatus;
    private String statusModifiedBy;
	private String fioSign1;
	private String fioSign2;
	private LocalDate dateSign1;
	private LocalDate dateSign2;
    private List<EcpPersonDTO> ecpPersons = new ArrayList<>();
//    private List<MoreSignsDTO> moreSigns = new ArrayList<>();

    public String getRejectMsg() {
        if(ecpPersons != null) {
            for(EcpPersonDTO ecpPerson : ecpPersons) {
                if (ecpPerson.getMsg() != null) return ecpPerson.getMsg();
            }
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getListNum() {
        return listNum;
    }

    public void setListNum(String listNum) {
        this.listNum = listNum;
    }

    public boolean isPaperShL() {
        return paperShL;
    }

    public void setPaperShL(boolean paperShL) {
        this.paperShL = paperShL;
    }

    public boolean isPaperShChTD() {
        return paperShChTD;
    }

    public void setPaperShChTD(boolean paperShChTD) {
        this.paperShChTD = paperShChTD;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public long getKbSize() {
        return size/1024;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public File.Status getStatus() {
        return status;
    }

    public String getStatusText() {
        return status.getText();
    }

    public void setStatus(File.Status status) {
        this.status = status;
    }

    public File.BranchType getBranchType() {
        return branchType;
    }

    public String getBranchTypeText() {
        return branchType.getText();
    }

    public void setBranchType(File.BranchType branchType) {
        this.branchType = branchType;
    }

    public int getSignedNum() {
        return signedNum;
    }

    public void setSignedNum(int signedNum) {
        this.signedNum = signedNum;
    }

    public int getTotalSignsNum() {
        return totalSignsNum;
    }

    public void setTotalSignsNum(int totalSignsNum) {
        this.totalSignsNum = totalSignsNum;
    }

    public boolean isCanBeSigned() {
        return canBeSigned;
    }

    public void setCanBeSigned(boolean canBeSigned) {
        this.canBeSigned = canBeSigned;
    }

    public boolean isNotSignedYet() {
        return notSignedYet;
    }

    public void setNotSignedYet(boolean notSignedYet) {
        this.notSignedYet = notSignedYet;
    }

    public boolean isHasPrevVersion() {
        return hasPrevVersion;
    }

    public void setHasPrevVersion(boolean hasPrevVersion) {
        this.hasPrevVersion = hasPrevVersion;
    }

    public List<EcpPersonDTO> getEcpPersons() {
        return ecpPersons;
    }

    public void setEcpPersons(List<EcpPersonDTO> ecpPersons) {
        this.ecpPersons = ecpPersons;
    }

    public boolean isMyOrderToSign() {
        return myOrderToSign;
    }

    public void setMyOrderToSign(boolean myOrderToSign) {
        this.myOrderToSign = myOrderToSign;
    }

    public String getRoutePositionStatus() {
        return routePositionStatus;
    }

    public void setRoutePositionStatus(String routePositionStatus) {
        this.routePositionStatus = routePositionStatus;
    }

    public boolean isHasNextVersion() {
        return hasNextVersion;
    }

    public void setHasNextVersion(boolean hasNextVersion) {
        this.hasNextVersion = hasNextVersion;
    }

    public Long getPrevVersionId() {
        return prevVersionId;
    }

    public void setPrevVersionId(Long prevVersionId) {
        this.prevVersionId = prevVersionId;
    }

    public Long getNextVersionId() {
        return nextVersionId;
    }

    public void setNextVersionId(Long nextVersionId) {
        this.nextVersionId = nextVersionId;
    }

    @Override
    public String toString() {
        return "FileBaseDTO{" +
          "id=" + id +
          ", status=" + status +
          ", signedNum=" + signedNum +
          ", branchType=" + branchType +
          ", listNum='" + listNum + '\'' +
          ", descr='" + descr + '\'' +
          ", name='" + name + '\'' +
          ", size=" + getKbSize() + "KB" +
          '}';
    }

    public String getStatusModifiedBy() {
        return statusModifiedBy;
    }

    public void setStatusModifiedBy(String statusModifiedBy) {
        this.statusModifiedBy = statusModifiedBy;
    }

    public Long getNextSignPositionId() {
        return nextSignPositionId;
    }

    public void setNextSignPositionId(Long nextSignPositionId) {
        this.nextSignPositionId = nextSignPositionId;
    }

	public Long getRoutePositionId() {
		return routePositionId;
	}

	public void setRoutePositionId(Long routePositionId) {
		this.routePositionId = routePositionId;
	}

	public String getFioSign1() {
		return fioSign1;
	}

	public void setFioSign1(String fioSign1) {
		this.fioSign1 = fioSign1;
	}

	public String getFioSign2() {
		return fioSign2;
	}

	public void setFioSign2(String fioSign2) {
		this.fioSign2 = fioSign2;
	}

	public LocalDate getDateSign1() {
		return dateSign1;
	}

	public void setDateSign1(LocalDate dateSign1) {
		this.dateSign1 = dateSign1;
	}

	public LocalDate getDateSign2() {
		return dateSign2;
	}

	public void setDateSign2(LocalDate dateSign2) {
		this.dateSign2 = dateSign2;
	}

	public boolean isMyOrderToSignAfterUpdate() {
		return myOrderToSignAfterUpdate;
	}

	public void setMyOrderToSignAfterUpdate(boolean myOrderToSignAfterUpdate) {
		this.myOrderToSignAfterUpdate = myOrderToSignAfterUpdate;
	}

	public boolean isOriginalCheckedSigned() {
		return originalCheckedSigned;
	}

	public void setOriginalCheckedSigned(boolean originalCheckedSigned) {
		this.originalCheckedSigned = originalCheckedSigned;
	}

	public String getNoteShl() {
		return noteShl;
	}

	public void setNoteShl(String noteShl) {
		this.noteShl = noteShl;
	}

	public String getThemeShchtd() {
		return themeShchtd;
	}

	public void setThemeShchtd(String themeShchtd) {
		this.themeShchtd = themeShchtd;
	}

	/*public List<MoreSignsDTO> getMoreSigns() {
        return moreSigns;
    }

    public void setMoreSigns(List<MoreSignsDTO> moreSigns) {
        this.moreSigns = moreSigns;
    }*/

    /*public int getMySignsNum() {
        return mySignsNum;
    }

    public void setMySignsNum(int mySignsNum) {
        this.mySignsNum = mySignsNum;
    }*/
}
