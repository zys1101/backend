package com.contact.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.contact.common.utils.IdGenerator;
import com.contact.entity.Contact;
import com.contact.entity.ContactTag;
import com.contact.entity.ContactTagRel;
import com.contact.mapper.ContactTagMapper;
import com.contact.mapper.ContactTagRelMapper;
import com.contact.entity.ContactPic;
import com.contact.entity.Matter;
import com.contact.entity.UserInfo;
import com.contact.mapper.ContactMapper;
import com.contact.mapper.ContactPicMapper;
import com.contact.mapper.MatterMapper;
import com.contact.mapper.UserInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试数据初始化器 — 仅在数据库为空时自动插入
 * <p>
 * 每次启动时先从DB查询已有最大ID，初始化IdGenerator计数器，
 * 避免应用重启后计数器重置导致主键冲突。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitRunner implements CommandLineRunner {

    private static final String PWD_RAW = "123456";

    private final UserInfoMapper userInfoMapper;
    private final ContactMapper contactMapper;
    private final MatterMapper matterMapper;
    private final ContactPicMapper contactPicMapper;
    private final ContactTagMapper contactTagMapper;
    private final ContactTagRelMapper contactTagRelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // ---- 每次启动时从DB初始化IdGenerator计数器 ----
        initIdGeneratorCounters();

        Long count = contactMapper.selectCount(null);
        if (count != null && count > 0) {
            log.info("测试数据已存在 (contacts={}), 跳过初始化", count);
            return;
        }
        log.info("开始初始化测试数据...");

        String encodedPwd = passwordEncoder.encode(PWD_RAW);
        List<UserInfo> users = initUsers(encodedPwd);
        List<Contact> contacts = initContacts(users);
        initMatters(contacts);

        log.info("测试数据初始化完成: users={}, contacts={}, matters={}",
                users.size(), contacts.size(),
                matterMapper.selectCount(null));
    }

    /**
     * 从数据库查询各表最大ID，初始化IdGenerator计数器。
     * 此方法必须在任何 insert 操作之前调用。
     */
    private void initIdGeneratorCounters() {
        String maxUserId = queryMaxUserId();
        String maxContactId = queryMaxContactId();
        String maxMatterId = queryMaxMatterId();
        String maxPictureId = queryMaxPictureId();
        String maxTagId = queryMaxTagId();

        IdGenerator.initFromExistingMaxId(maxUserId, maxContactId, maxMatterId, maxPictureId, maxTagId);
        log.info("IdGenerator计数器已从DB初始化: user={}, contact={}, matter={}, picture={}, tag={}",
                maxUserId, maxContactId, maxMatterId, maxPictureId, maxTagId);
    }

    private String queryMaxUserId() {
        LambdaQueryWrapper<UserInfo> w = new LambdaQueryWrapper<>();
        w.select(UserInfo::getUserId).orderByDesc(UserInfo::getUserId).last("LIMIT 1");
        UserInfo entity = userInfoMapper.selectOne(w);
        return entity != null ? entity.getUserId() : null;
    }

    private String queryMaxContactId() {
        LambdaQueryWrapper<Contact> w = new LambdaQueryWrapper<>();
        w.select(Contact::getCtId).orderByDesc(Contact::getCtId).last("LIMIT 1");
        Contact entity = contactMapper.selectOne(w);
        return entity != null ? entity.getCtId() : null;
    }

    private String queryMaxMatterId() {
        LambdaQueryWrapper<Matter> w = new LambdaQueryWrapper<>();
        w.select(Matter::getMatterId).orderByDesc(Matter::getMatterId).last("LIMIT 1");
        Matter entity = matterMapper.selectOne(w);
        return entity != null ? entity.getMatterId() : null;
    }

    private String queryMaxPictureId() {
        LambdaQueryWrapper<ContactPic> w = new LambdaQueryWrapper<>();
        w.select(ContactPic::getPicId).orderByDesc(ContactPic::getPicId).last("LIMIT 1");
        ContactPic entity = contactPicMapper.selectOne(w);
        return entity != null ? entity.getPicId() : null;
    }

    private String queryMaxTagId() {
        LambdaQueryWrapper<ContactTag> w = new LambdaQueryWrapper<>();
        w.select(ContactTag::getTagId).orderByDesc(ContactTag::getTagId).last("LIMIT 1");
        ContactTag entity = contactTagMapper.selectOne(w);
        return entity != null ? entity.getTagId() : null;
    }

    // ==================== 用户 ====================

    private List<UserInfo> initUsers(String encodedPwd) {
        String[][] raw = {
                {"admin",    "管理员"},
                {"zhangsan", "张三"},
                {"lisi",     "李四"},
                {"wangwu",   "王五"},
        };
        List<UserInfo> list = new ArrayList<>();
        for (String[] r : raw) {
            UserInfo u = new UserInfo();
            u.setUserId(IdGenerator.generateUserId());
            u.setUsername(r[0]);
            u.setUserPassword(encodedPwd);
            u.setStatus(1);
            u.setCreatedAt(LocalDateTime.now());
            u.setUpdatedAt(LocalDateTime.now());
            userInfoMapper.insert(u);
            list.add(u);
            log.debug("用户: {} / {}", u.getUserId(), r[0]);
        }
        return list;
    }

    // ==================== 联系人 ====================

    private List<Contact> initContacts(List<UserInfo> users) {
        String adminId    = users.get(0).getUserId();
        String zhangsanId = users.get(1).getUserId();
        String lisiId     = users.get(2).getUserId();

        // admin — 22个联系人 (含4个黑名单)
        String[][] adminRaw = {
                {"陈伟",   "男", "13901001001", "chenwei@qq.com",   "1988-03-15", "北京市海淀区中关村大街1号",    "100080", "12345678",  "chenwei_wx"},
                {"李娜",   "女", "13901001002", "lina@163.com",     "1992-07-22", "上海市静安区南京西路1688号",   "200040", "23456789",  "lina_nana"},
                {"王磊",   "男", "13901001003", "wanglei@gmail.com", "1985-11-08", "广州市天河区体育西路111号",    "510620", "34567890",  "wanglei_gz"},
                {"赵敏",   "女", "13901001004", "zhaomin@qq.com",   "1995-01-30", "深圳市南山区科技园南路2号",    "518057", "45678901",  "zhaomin_sz"},
                {"刘洋",   "男", "13901001005", "liuyang@126.com",  "1990-05-18", "杭州市西湖区文三路478号",      "310012", "56789012",  "liuyang_hz"},
                {"孙丽",   "女", "13901001006", "sunli@hotmail.com","1993-09-12", "成都市武侯区人民南路四段1号",  "610041", "67890123",  "sunli_cd"},
                {"周杰",   "男", "13901001007", "zhoujie@qq.com",   "1982-12-03", "武汉市洪山区珞喻路1037号",     "430074", "78901234",  "zhoujie_wh"},
                {"吴芳",   "女", "13901001008", "wufang@163.com",   "1997-04-25", "南京市玄武区珠江路700号",      "210018", "89012345",  "wufang_nj"},
                {"郑强",   "男", "13901001009", "zhengqiang@qq.com","1978-08-14", "西安市雁塔区小寨东路88号",     "710061", "90123456",  "zhengq_xa"},
                {"冯婷",   "女", "13901001010", "fengting@gmail.com","1991-06-07","重庆市渝中区解放碑步行街1号",  "400010", "01234567",  "fengting_cq"},
                {"黄明",   "男", "13901001011", "huangming@qq.com", "1986-02-28", "长沙市岳麓区麓山南路932号",    "410082", "11122334",  "huangm_cs"},
                {"韩雪",   "女", "13901001012", "hanxue@163.com",   "1994-10-16", "天津市和平区滨江道200号",      "300041", "22233445",  "hanxue_tj"},
                {"林涛",   "男", "13901001013", "lintao@126.com",   "1989-07-09", "苏州市姑苏区观前街88号",       "215005", "33344556",  "lintao_sz"},
                {"秦岚",   "女", "13901001014", "qinlan@qq.com",    "1996-03-21", "郑州市金水区花园路39号",       "450003", "44455667",  "qinlan_zz"},
                {"许建",   "男", "13901001015", "xujian@163.com",   "1984-01-11", "济南市历下区泉城路180号",      "250011", "55566778",  "xujian_jn"},
                {"沈洁",   "女", "13901001016", "shenjie@qq.com",   "1998-08-05", "厦门市思明区鹭江道100号",      "361001", "66677889",  "shenjie_xm"},
                {"曹宇",   "男", "13901001017", "caoyu@gmail.com",  "1981-05-27", "合肥市庐阳区长江中路98号",     "230001", "77788990",  "caoyu_hf"},
                {"董悦",   "女", "13901001018", "dongyue@126.com",  "1999-12-31", "青岛市市南区香港中路69号",     "266071", "88899001",  "dongyue_qd"},
                {"丁磊",   "男", "13901001019", "dingl@qq.com",     "1979-09-19", "大连市中山区人民路50号",       "116001", "99900112",  "dingl_dl"},
                {"田雨",   "女", "13901001020", "tianyu@163.com",   "1992-02-14", "昆明市五华区东风西路2号",      "650031", "00011223",  "tianyu_km"},
                {"潘鹏",   "男", "13901001021", "panpeng@qq.com",   "1987-06-23", "南昌市东湖区八一大道356号",    "330006", "11133445",  "panpeng_nc"},
                {"蒋文",   "女", "13901001022", "jiangwen@126.com", "2000-04-01", "福州市鼓楼区五四路158号",      "350003", "22244556",  "jiangwen_fz"},
        };

        // zhangsan — 8个联系人 (含1个黑名单)
        String[][] zhangsanRaw = {
                {"宋峰", "男", "13902002001", "songfeng@qq.com",  "1990-10-10", "哈尔滨市南岗区学府路74号",     "150080", "songf_hb",  "33355667"},
                {"唐玲", "女", "13902002002", "tangling@163.com", "1993-08-18", "长春市朝阳区工农大路500号",    "130021", "tangl_cc",  "44466778"},
                {"侯刚", "男", "13902002003", "hougang@126.com",  "1986-04-05", "福州市鼓楼区五四路158号",      "350003", "houg_fz",   "55577889"},
                {"龙娇", "女", "13902002004", "longjiao@qq.com",  "1997-12-12", "贵阳市云岩区中华北路200号",    "550001", "longj_gy",  "66688990"},
                {"万鑫", "男", "13902002005", "wanxin@gmail.com", "1983-07-29", "兰州市城关区张掖路87号",       "730030", "wanx_lz",   "77799001"},
                {"段颖", "女", "13902002006", "duanying@163.com", "1995-05-06", "乌鲁木齐市天山区解放南路1号",  "830002", "duany_wlmq","88800112"},
                {"雷震", "男", "13902002007", "leizhen@qq.com",   "1980-11-20", "南宁市青秀区民族大道120号",    "530022", "leiz_nn",   "99911223"},
                {"夏蓉", "女", "13902002008", "xiarong@126.com",  "2001-01-08", "海口市龙华区滨海大道95号",     "570105", "xiar_hk",   "00022334"},
        };

        // lisi — 6个联系人
        String[][] lisiRaw = {
                {"谢辉", "男", "13903003001", "xiehui@163.com",   "1991-09-03", "呼和浩特市新城区新华大街1号",  "010050", "xieh_hu",   "11144556"},
                {"邹萍", "女", "13903003002", "zouping@qq.com",   "1996-02-28", "拉萨市城关区北京中路20号",     "850000", "zoup_ls",   "22255667"},
                {"苏晨", "男", "13903003003", "suchen@126.com",   "1988-04-17", "西宁市城西区西关大街36号",     "810001", "suc_xn",    "33366778"},
                {"范丽", "女", "13903003004", "fanli@gmail.com",  "1994-06-21", "银川市兴庆区解放西街10号",     "750001", "fanl_yc",   "44477889"},
                {"鲁安", "男", "13903003005", "luan@qq.com",      "1982-08-30", "太原市杏花岭区府西街3号",      "030002", "lua_ty",    "55588990"},
                {"袁琴", "女", "13903003006", "yuanqin@163.com",  "1999-11-15", "长沙市天心区劳动西路288号",    "410002", "yuanq_cs2", "66699001"},
        };

        List<Contact> allContacts = new ArrayList<>();

        // admin联系人 — 前4个黑名单，后18个正常
        for (int i = 0; i < adminRaw.length; i++) {
            Contact c = buildContact(adminRaw[i], adminId);
            c.setCtDelete(i < 4 ? 1 : 0);
            contactMapper.insert(c);
            allContacts.add(c);
        }
        // zhangsan联系人 — 第1个黑名单
        for (int i = 0; i < zhangsanRaw.length; i++) {
            Contact c = buildContact(zhangsanRaw[i], zhangsanId);
            c.setCtDelete(i == 0 ? 1 : 0);
            contactMapper.insert(c);
            allContacts.add(c);
        }
        // lisi联系人 — 全部正常
        for (String[] r : lisiRaw) {
            Contact c = buildContact(r, lisiId);
            c.setCtDelete(0);
            contactMapper.insert(c);
            allContacts.add(c);
        }

        log.debug("联系人总数: {}", allContacts.size());
        return allContacts;
    }

    private Contact buildContact(String[] r, String userId) {
        Contact c = new Contact();
        c.setCtId(IdGenerator.generateContactId());
        c.setUserId(userId);
        c.setCtName(r[0]);
        c.setCtMf(r[1]);
        c.setCtPhone(r[2]);
        c.setCtEm(r[3]);
        c.setCtBirth(LocalDate.parse(r[4]));
        c.setCtAd(r[5]);
        c.setCtYb(r[6]);
        c.setCtQq(r[7]);
        c.setCtWx(r[8]);
        c.setCreatedAt(LocalDateTime.now().minusDays((long) (Math.random() * 60)));
        c.setUpdatedAt(c.getCreatedAt());
        return c;
    }

    // ==================== 事项 ====================

    private void initMatters(List<Contact> contacts) {
        List<Matter> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // matterContent -> matterDelete 的模板对
        Object[][] templates = {
                {"开会讨论", 0}, {"项目验收", 0}, {"合同签订", 2}, {"客户拜访", 1}, {"方案评审", 0},
                {"生日聚会", 0}, {"技术交流", 2}, {"产品演示", 1}, {"商务会谈", 0}, {"需求调研", 2},
                {"定期回访", 0}, {"代码审查", 2}, {"年度总结", 0}, {"团建活动", 0}, {"培训讲座", 1},
                {"出差安排", 2}, {"线上会议", 0}, {"接待来访", 0}, {"绩效面谈", 2}, {"系统上线", 0},
                {"版本发布", 2}, {"数据迁移", 1}, {"安全审计", 0}, {"专利申请", 0}, {"论文答辩", 2},
        };

        for (int i = 0; i < contacts.size(); i++) {
            Contact ct = contacts.get(i);
            Object[] tpl = templates[i % templates.length];

            // 每个联系人 2-3 个事项
            int matterCount = 2 + (i % 2);
            for (int j = 0; j < matterCount; j++) {
                Matter m = new Matter();
                m.setMatterId(IdGenerator.generateMatterId());
                m.setCtId(ct.getCtId());
                m.setUserId(ct.getUserId());
                m.setMatterTime(now.plusDays(i + j * 3 - 10));
                m.setMatter(tpl[0] + " - " + ct.getCtName());
                m.setMatterDelete((Integer) tpl[1]);
                m.setCreatedAt(now.minusDays(i + j));
                m.setUpdatedAt(now.minusHours(i));
                matterMapper.insert(m);
                list.add(m);
            }
        }
        log.debug("事项总数: {}", list.size());
    }
}
