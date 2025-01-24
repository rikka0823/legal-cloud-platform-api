package SmartLegalSearch.constants;

public enum Crime {

    // 常規罪刑
    MURDER("殺人罪"), //
    INJURY("傷害罪"), //
    ROBBERY("強盜罪"), //
    THEFT("竊盜罪"), //
    FRAUD("詐欺罪"), //
    EXTORTION("搶奪罪"), //
    SEXUAL_ASSAULT("性侵害罪"), //
    PUBLIC_DANGER("公共危險罪"), //
    CORRUPTION("貪污罪"), //
    FORGERY_DOCUMENT("偽造文書罪"), //
    FORGERY_CURRENCY("偽造貨幣罪"), //
    PERJURY("偽證罪"), //
    BRIBERY_RECEIVING("受賄罪"), //
    ARSON("縱火罪"), //
    THEFT_PUBLIC_ASSETS("竊取公共財物罪"), //
    DRUG_CRIMES("毒品犯罪"), //
    FORGERY_COMPANY_SEAL("偽造公司印章罪"), //
    BRIBERY_GIVING("行賄罪"), //
    STEALING("偷竊罪"), //
    BRIBERY("賄賂罪"), //
    FAMILY_OFFENSE("妨害家庭罪"), //
    INSULT("公然侮辱罪"), //
    EMBEZZLEMENT("侵占罪"), //
    HIT_AND_RUN("肇事逃逸罪"),
    NEGLIGENCE_CAUSING_DEATH("過失致死罪"),
    NEGLIGENCE_CAUSING_INJURY("過失傷害罪"),

    // 特殊法律的罪刑
    REBELLION("內亂罪"), //
    TREASON("外患罪"), //
    NEGLIGENCE("瀆職罪"), //
    OBSTRUCTION_OF_OFFICE("妨害公務罪"), //
    CONTEMPT_OF_PARLIAMENT("藐視國會罪"), //
    VOTING_OBSTRUCTION("妨害投票罪"), //
    ESCAPE("脫逃罪"), //
    HARBORING_CRIMINALS("藏匿人犯及湮滅證據罪"), //
    SACRILEGE_AND_VANDALISM("褻瀆祀典及侵害墳墓屍體罪"), //
    OBSTRUCTION_OF_AGRICULTURE("妨害農工商罪"), //
    OPIUM_CRIMES("鴉片罪"), //
    GAMBLING("賭博罪"), //
    COMPUTER_CRIMES("妨害電腦使用罪");


    private String crimeName;

    Crime(String crimeName) {
        this.crimeName = crimeName;
    }

    public String getCrimeName() {
        return crimeName;
    }
}
