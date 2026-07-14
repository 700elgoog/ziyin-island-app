package com.ziyin.island.content

data class PinyinLesson(
    val id: String,
    val stage: String,
    val title: String,
    val symbols: List<String>,
    val mission: String,
    val examples: List<String>,
)

data class CharacterLesson(
    val character: String,
    val pinyin: String,
    val category: String,
    val meaning: String,
    val words: List<String>,
    val picture: String,
)

data class StoryLesson(
    val id: String,
    val title: String,
    val focus: String,
    val pages: List<String>,
)

data class RhymeLesson(
    val id: String,
    val title: String,
    val soundFocus: String,
    val lines: List<String>,
)

object Curriculum {
    val pinyinLessons: List<PinyinLesson> = buildList {
        add(PinyinLesson("A01", "声音准备", "声音有长短", listOf("—", "·"), "听一听、拍一拍，发现声音的长短。", listOf("风声", "雨声", "钟声")))
        add(PinyinLesson("A02", "声音准备", "声音会爬坡", listOf("ˉ", "ˊ", "ˇ", "ˋ"), "用手指走过四条声音轨道。", listOf("高平", "上扬", "转弯", "下降")))

        listOf(
            Triple("a", "张大嘴巴，声音响亮。", listOf("大", "马", "爸")),
            Triple("o", "嘴唇圆圆，像个小圆圈。", listOf("我", "火", "朵")),
            Triple("e", "嘴巴扁扁，声音从喉咙出来。", listOf("鹅", "河", "乐")),
            Triple("i", "牙齿对齐，嘴角向两边。", listOf("一", "米", "鸡")),
            Triple("u", "嘴唇突出，像吹小口哨。", listOf("五", "木", "兔")),
            Triple("ü", "嘴唇圆圆，舌头向前。", listOf("鱼", "雨", "绿")),
        ).forEachIndexed { index, item ->
            add(PinyinLesson("B%02d".format(index + 1), "单韵母", "认识 ${item.first}", listOf(item.first), item.second, item.third))
        }

        val initials = listOf(
            "b" to listOf("爸", "笔", "包"), "p" to listOf("跑", "皮", "葡"),
            "m" to listOf("妈", "米", "木"), "f" to listOf("风", "飞", "房"),
            "d" to listOf("大", "灯", "朵"), "t" to listOf("天", "兔", "桃"),
            "n" to listOf("你", "牛", "鸟"), "l" to listOf("来", "蓝", "鹿"),
            "g" to listOf("哥", "高", "鼓"), "k" to listOf("开", "口", "看"),
            "h" to listOf("好", "花", "河"), "j" to listOf("家", "鸡", "九"),
            "q" to listOf("七", "桥", "球"), "x" to listOf("小", "星", "西"),
            "zh" to listOf("纸", "竹", "桌"), "ch" to listOf("车", "虫", "船"),
            "sh" to listOf("山", "手", "书"), "r" to listOf("日", "人", "热"),
            "z" to listOf("字", "早", "走"), "c" to listOf("草", "菜", "从"),
            "s" to listOf("三", "松", "伞"), "y" to listOf("一", "月", "云"),
            "w" to listOf("五", "我", "玩"),
        )
        initials.forEachIndexed { index, item ->
            add(PinyinLesson("C%02d".format(index + 1), "声母", "声母 ${item.first}", listOf(item.first), "听清短短的声母，再和韵母交朋友。", item.second))
        }

        listOf("ai", "ei", "ui", "ao", "ou", "iu", "ie", "üe", "er").forEachIndexed { index, final ->
            add(PinyinLesson("D%02d".format(index + 1), "复韵母", "复韵母 $final", listOf(final), "嘴形从前一个音滑向后一个音。", examplesFor(final)))
        }
        listOf("an", "en", "in", "un", "ün", "ang", "eng", "ing", "ong").forEachIndexed { index, final ->
            add(PinyinLesson("E%02d".format(index + 1), "鼻韵母", "鼻韵母 $final", listOf(final), "让声音从口腔轻轻走向鼻腔。", examplesFor(final)))
        }

        listOf(
            listOf("zhi", "chi", "shi", "ri"),
            listOf("zi", "ci", "si"),
            listOf("yi", "wu", "yu"),
            listOf("ye", "yue", "yuan"),
            listOf("yin", "yun", "ying"),
        ).forEachIndexed { index, group ->
            add(PinyinLesson("F%02d".format(index + 1), "整体认读", "整体认读 ${index + 1}", group, "这些音节要连在一起读，不能拆开拼。", group))
        }

        add(PinyinLesson("G01", "拼读规则", "两拼法", listOf("b", "a", "ba"), "声母轻短，韵母响亮，两音相连猛一碰。", listOf("爸爸", "妈妈", "大米")))
        add(PinyinLesson("G02", "拼读规则", "三拼法", listOf("g", "u", "a", "gua"), "声轻、介快、韵母响，三音连读很顺当。", listOf("西瓜", "花朵", "火锅")))
        add(PinyinLesson("G03", "拼读规则", "ü 的礼貌帽", listOf("j", "q", "x", "ü"), "小 ü 见到 j、q、x，去掉两点还读 ü。", listOf("句", "去", "许")))
        add(PinyinLesson("G04", "拼读规则", "声调住哪里", listOf("a", "o", "e", "i", "u", "ü"), "有 a 不放过，没 a 找 o、e，i、u 并排标在后。", listOf("hǎo", "liú", "guī")))
        add(PinyinLesson("G05", "综合复习", "拼音寻宝", listOf("听", "拼", "读"), "听声找音、看图拼读、把音节送回家。", listOf("山水", "花草", "日月")))
        add(PinyinLesson("G06", "综合复习", "毕业旅行", listOf("读", "认", "讲"), "用拼音读一句话，再讲给小芽鸟听。", listOf("我爱我家", "小鱼游水", "白云在天上")))
    }

    private val coreCharacterLessons: List<CharacterLesson> = listOf(
        entry("日", "rì", "自然", "太阳", "日光/日出", "☀️"), entry("月", "yuè", "自然", "月亮", "月光/月牙", "🌙"),
        entry("山", "shān", "自然", "高高的山", "山顶/火山", "⛰️"), entry("水", "shuǐ", "自然", "流动的水", "河水/喝水", "💧"),
        entry("火", "huǒ", "自然", "发光发热的火", "火苗/火车", "🔥"), entry("木", "mù", "自然", "树木", "木头/树木", "🌳"),
        entry("土", "tǔ", "自然", "土地和泥土", "土地/泥土", "🪴"), entry("云", "yún", "自然", "天上的云", "白云/云朵", "☁️"),
        entry("雨", "yǔ", "自然", "从云里落下的水滴", "下雨/雨衣", "🌧️"), entry("风", "fēng", "自然", "流动的空气", "大风/风车", "🌬️"),
        entry("天", "tiān", "自然", "头顶的天空", "天空/今天", "🌤️"), entry("田", "tián", "自然", "种庄稼的土地", "田地/水田", "🌾"),
        entry("人", "rén", "家人", "我们每一个人", "大人/人们", "🧑"), entry("大", "dà", "家人", "和小相对", "大人/长大", "🐘"),
        entry("小", "xiǎo", "家人", "和大相对", "小鸟/大小", "🐥"), entry("爸", "bà", "家人", "爸爸", "爸爸/爸妈", "👨"),
        entry("妈", "mā", "家人", "妈妈", "妈妈/爸妈", "👩"), entry("爷", "yé", "家人", "爷爷", "爷爷/老爷爷", "👴"),
        entry("奶", "nǎi", "家人", "奶奶，也表示牛奶", "奶奶/牛奶", "🥛"), entry("哥", "gē", "家人", "哥哥", "哥哥/大哥", "👦"),
        entry("姐", "jiě", "家人", "姐姐", "姐姐/姐妹", "👧"), entry("弟", "dì", "家人", "弟弟", "弟弟/兄弟", "🧒"),
        entry("妹", "mèi", "家人", "妹妹", "妹妹/姐妹", "👧"), entry("家", "jiā", "家人", "生活的地方和家人", "回家/大家", "🏠"),
        entry("口", "kǒu", "身体", "嘴巴", "开口/门口", "👄"), entry("耳", "ěr", "身体", "耳朵", "耳朵/木耳", "👂"),
        entry("目", "mù", "身体", "眼睛", "目光/双目", "👁️"), entry("手", "shǒu", "身体", "拿东西的手", "小手/洗手", "✋"),
        entry("足", "zú", "身体", "脚", "足球/手足", "🦶"), entry("头", "tóu", "身体", "身体最上面的部分", "头发/点头", "🙂"),
        entry("心", "xīn", "身体", "心脏，也表示心情", "开心/爱心", "❤️"), entry("牙", "yá", "身体", "嘴里的牙齿", "牙齿/刷牙", "🦷"),
        entry("一", "yī", "数字", "一个", "一天/第一", "1️⃣"), entry("二", "èr", "数字", "两个", "二月/第二", "2️⃣"),
        entry("三", "sān", "数字", "三个", "三人/第三", "3️⃣"), entry("四", "sì", "数字", "四个", "四方/四月", "4️⃣"),
        entry("五", "wǔ", "数字", "五个", "五月/五人", "5️⃣"), entry("六", "liù", "数字", "六个", "六月/六只", "6️⃣"),
        entry("七", "qī", "数字", "七个", "七月/七彩", "7️⃣"), entry("八", "bā", "数字", "八个", "八方/八只", "8️⃣"),
        entry("九", "jiǔ", "数字", "九个", "九月/九只", "9️⃣"), entry("十", "shí", "数字", "十个", "十月/十分", "🔟"),
        entry("上", "shàng", "方位", "位置较高", "上面/上学", "⬆️"), entry("下", "xià", "方位", "位置较低", "下面/下雨", "⬇️"),
        entry("左", "zuǒ", "方位", "面对前方时的一边", "左手/左边", "⬅️"), entry("右", "yòu", "方位", "面对前方时的另一边", "右手/右边", "➡️"),
        entry("中", "zhōng", "方位", "中间", "中间/中国", "⏺️"), entry("前", "qián", "方位", "面对的方向", "前面/向前", "⏩"),
        entry("后", "hòu", "方位", "背对的方向", "后面/以后", "⏪"), entry("里", "lǐ", "方位", "内部", "里面/家里", "📦"),
        entry("花", "huā", "动物植物", "植物开的花", "花朵/红花", "🌸"), entry("草", "cǎo", "动物植物", "小草", "草地/青草", "🌱"),
        entry("鸟", "niǎo", "动物植物", "有羽毛的动物", "小鸟/飞鸟", "🐦"), entry("鱼", "yú", "动物植物", "生活在水里的动物", "小鱼/金鱼", "🐟"),
        entry("马", "mǎ", "动物植物", "会奔跑的动物", "小马/白马", "🐴"), entry("牛", "niú", "动物植物", "力气大的动物", "黄牛/牛奶", "🐮"),
        entry("羊", "yáng", "动物植物", "有卷毛的动物", "山羊/羊毛", "🐑"), entry("虫", "chóng", "动物植物", "小虫子", "虫子/昆虫", "🐛"),
        entry("猫", "māo", "动物植物", "会喵喵叫的动物", "小猫/花猫", "🐱"), entry("狗", "gǒu", "动物植物", "会汪汪叫的动物", "小狗/黄狗", "🐶"),
    )

    val characterLessons: List<CharacterLesson> = coreCharacterLessons + additionalCharacterLessons()

    val stories: List<StoryLesson> = listOf(
        story("S01", "啊——山谷回声", "a 的四声", "小芽鸟对着山谷张大嘴巴：啊——！", "声音飞过小河，又从山那边跑回来。", "小芽鸟用四种声调问好，山谷也用四种声调回答。"),
        story("S02", "圆圆的月亮船", "o", "月亮像一条圆圆的小船，慢慢升上天空。", "小鱼把嘴唇圆起来，唱出长长的 o。", "水面也画出一个圆圆的倒影。"),
        story("S03", "白鹅过小河", "e", "一只白鹅伸长脖子，走到清清的小河边。", "它看见水里的自己，轻轻唱起 e。", "河水带着歌声流向远方。"),
        story("S04", "小雨点找朋友", "ü", "小雨点从云朵跳下来，落在小鱼身边。", "小鱼鼓起圆圆的嘴巴，邀请雨点一起玩。", "雨停了，天边挂起一道彩虹。"),
        story("S05", "爸爸的八朵花", "b / p", "爸爸带回八颗小种子。", "大家把种子轻轻埋进花盆。", "春风一吹，八朵花一起开放。"),
        story("S06", "妈妈的米香", "m", "清晨，厨房里飘来暖暖的米香。", "妈妈盛好小米粥，叫大家来吃饭。", "我说：谢谢妈妈，真香！"),
        story("S07", "风筝飞过山", "f", "小风筝跟着风，飞呀飞。", "它飞过小河，飞过树林，又飞到山顶。", "太阳下山前，风筝安全回到小朋友手里。"),
        story("S08", "三只小动物", "z / c / s", "三只小动物在草地上发现一篮种子。", "它们一起松土、浇水，谁也不抢。", "种子长成一片绿色的小菜园。"),
        story("S09", "竹林里的纸船", "zh / ch / sh", "竹林边的小池塘里，漂来一只纸船。", "小虫坐上船，小鸟在岸边守护它。", "纸船绕过石头，平平安安回到岸边。"),
        story("S10", "西瓜分一分", "三拼音节", "小熊抱来一个大西瓜。", "它把西瓜切开，请所有朋友一起吃。", "大家边吃边说：西瓜又甜又香。"),
        story("S11", "白云住在天上", "整体认读", "白云住在蓝蓝的天上。", "风来了，白云变成鱼、变成马、变成一朵花。", "傍晚，白云穿上金色的衣裳。"),
        story("S12", "字音岛的一天", "综合阅读", "太阳升起，小芽鸟叫醒字音岛。", "孩子们听声音、拼音节、认识汉字，还读了一个故事。", "星星亮起时，大家约好明天继续探索。"),
    )

    val rhymes: List<RhymeLesson> = listOf(
        rhyme("R01", "拍手歌", "长短节奏", "拍一拍，停一停", "小手听清再出发"),
        rhyme("R02", "小雨滴", "强弱", "小雨轻轻滴答答", "大雨拍拍大荷花"),
        rhyme("R03", "风来了", "高低", "风从山下慢慢来", "爬上树梢飞起来"),
        rhyme("R04", "回声谷", "模仿声音", "我说你好山回答", "长音短音学一学"),
        rhyme("R05", "张大嘴巴", "a", "张大嘴巴 a a a", "山谷开出喇叭花"),
        rhyme("R06", "圆圆泡泡", "o", "圆圆嘴巴 o o o", "泡泡飘过小山坡"),
        rhyme("R07", "白鹅唱歌", "e", "白鹅水中 e e e", "弯弯脖子唱支歌"),
        rhyme("R08", "小蜡烛", "i", "一根蜡烛 i i i", "照亮夜空亮晶晶"),
        rhyme("R09", "小乌龟", "u", "小小乌龟 u u u", "背着房子去散步"),
        rhyme("R10", "小鱼吐泡", "ü", "小鱼小鱼 ü ü ü", "圆圆泡泡浮上去"),
        rhyme("R11", "右下半圆", "b / p", "右下半圆 b b b", "小旗迎风 p p p"),
        rhyme("R12", "两个门洞", "m / f", "两个门洞 m m m", "伞把弯弯 f f f"),
        rhyme("R13", "小马跑", "d / t", "小马跑来 d d d", "伞把朝下 t t t"),
        rhyme("R14", "门和小棍", "n / l", "一个门洞 n n n", "一根小棍 l l l"),
        rhyme("R15", "鸽子蝌蚪", "g / k / h", "白鸽衔环 g g g", "蝌蚪水草 k k k"),
        rhyme("R16", "小鸡气球", "j / q / x", "小鸡抬头 j j j", "气球一串 q q q"),
        rhyme("R17", "织衣吃果", "zh / ch / sh / r", "织件毛衣 zh zh zh", "吃个苹果 ch ch ch"),
        rhyme("R18", "小刺猬", "z / c / s", "小小数字 z z z", "刺猬弯腰 c c c"),
        rhyme("R19", "挨在一起", "ai / ei / ui", "你挨我来 ai ai ai", "杯子举起 ei ei ei"),
        rhyme("R20", "棉袄海鸥", "ao / ou / iu", "穿上棉袄 ao ao ao", "海鸥飞来 ou ou ou"),
        rhyme("R21", "月儿耳朵", "ie / üe / er", "树叶摇摇 ie ie ie", "月儿弯弯 üe üe üe"),
        rhyme("R22", "前鼻音", "an / en / in / un / ün", "天安门前 an an an", "轻轻按门 en en en"),
        rhyme("R23", "后鼻音", "ang / eng / ing / ong", "山羊唱歌 ang ang ang", "风吹灯笼 eng eng eng"),
        rhyme("R24", "拼音旅行", "综合拼读", "声母轻，韵母响", "两音相连读得亮"),
    )

    private fun examplesFor(final: String): List<String> = when (final) {
        "ai" -> listOf("白", "海", "开"); "ei" -> listOf("飞", "黑", "杯"); "ui" -> listOf("水", "回", "龟")
        "ao" -> listOf("猫", "桃", "高"); "ou" -> listOf("口", "手", "走"); "iu" -> listOf("牛", "球", "六")
        "ie" -> listOf("叶", "鞋", "姐"); "üe" -> listOf("月", "雪", "学"); "er" -> listOf("耳", "二", "儿")
        "an" -> listOf("山", "伞", "蓝"); "en" -> listOf("门", "人", "本"); "in" -> listOf("林", "心", "金")
        "un" -> listOf("云", "轮", "春"); "ün" -> listOf("云", "群", "裙"); "ang" -> listOf("羊", "房", "糖")
        "eng" -> listOf("风", "灯", "冷"); "ing" -> listOf("星", "听", "冰"); "ong" -> listOf("虫", "钟", "龙")
        else -> emptyList()
    }

    private fun entry(c: String, p: String, category: String, meaning: String, words: String, picture: String) =
        CharacterLesson(c, p, category, meaning, words.split('/'), picture)

    private fun additionalCharacterLessons(): List<CharacterLesson> {
        val groups = linkedMapOf(
            "我和大家" to "我:wǒ 你:nǐ 他:tā 她:tā 它:tā 们:men 友:yǒu 朋:péng 师:shī 生:shēng 男:nán 女:nǚ 老:lǎo 少:shào 叔:shū 姨:yí 姑:gū 舅:jiù 孙:sūn 邻:lín 客:kè 主:zhǔ 伴:bàn 医:yī 工:gōng 农:nóng 民:mín 司:sī 机:jī 谁:shuí",
            "动作表达" to "来:lái 去:qù 走:zǒu 跑:pǎo 跳:tiào 坐:zuò 站:zhàn 看:kàn 听:tīng 说:shuō 读:dú 写:xiě 画:huà 唱:chàng 笑:xiào 哭:kū 吃:chī 喝:hē 睡:shuì 起:qǐ 开:kāi 关:guān 拿:ná 放:fàng 给:gěi 问:wèn 答:dá 想:xiǎng 知:zhī 道:dào",
            "学习生活" to "学:xué 习:xí 会:huì 做:zuò 洗:xǐ 穿:chuān 脱:tuō 推:tuī 拉:lā 买:mǎi 卖:mài 找:zhǎo 回:huí 进:jìn 出:chū 过:guò 住:zhù 用:yòng 叫:jiào 让:ràng 请:qǐng 谢:xiè 对:duì 没:méi 很:hěn 真:zhēn 能:néng 要:yào 得:dé 把:bǎ",
            "时间天气" to "年:nián 春:chūn 夏:xià 秋:qiū 冬:dōng 早:zǎo 晚:wǎn 今:jīn 明:míng 昨:zuó 时:shí 分:fēn 秒:miǎo 星:xīng 期:qī 周:zhōu 午:wǔ 晨:chén 夜:yè 晴:qíng 阴:yīn 雪:xuě 雷:léi 电:diàn 光:guāng 热:rè 冷:lěng 暖:nuǎn 久:jiǔ 先:xiān",
            "校园物品" to "书:shū 本:běn 纸:zhǐ 笔:bǐ 尺:chǐ 桌:zhuō 椅:yǐ 门:mén 窗:chuāng 包:bāo 课:kè 校:xiào 班:bān 园:yuán 图:tú 字:zì 音:yīn 歌:gē 球:qiú 灯:dēng 车:chē 船:chuán 路:lù 桥:qiáo 房:fáng 床:chuáng 衣:yī 裤:kù 鞋:xié 帽:mào",
            "吃饭穿衣" to "伞:sǎn 杯:bēi 碗:wǎn 勺:sháo 饭:fàn 面:miàn 菜:cài 果:guǒ 苹:píng 桃:táo 瓜:guā 豆:dòu 蛋:dàn 肉:ròu 茶:chá 糖:táng 盐:yán 油:yóu 汤:tāng 饼:bǐng 香:xiāng 甜:tián 酸:suān 苦:kǔ 饱:bǎo 饿:è 袜:wà 裙:qún 衫:shān 角:jiǎo",
            "颜色样子" to "红:hóng 橙:chéng 黄:huáng 绿:lǜ 青:qīng 蓝:lán 紫:zǐ 白:bái 黑:hēi 灰:huī 多:duō 高:gāo 长:cháng 短:duǎn 方:fāng 圆:yuán 直:zhí 弯:wān 胖:pàng 瘦:shòu 美:měi 丑:chǒu 干:gān 湿:shī 亮:liàng 暗:àn 轻:qīng 重:zhòng 快:kuài 慢:màn",
            "动物朋友" to "虎:hǔ 兔:tù 龙:lóng 蛇:shé 猴:hóu 猪:zhū 鹿:lù 熊:xióng 象:xiàng 狮:shī 狐:hú 狼:láng 鼠:shǔ 鸭:yā 鹅:é 鸡:jī 蛙:wā 蝶:dié 蜂:fēng 蚁:yǐ 蚕:cán 燕:yàn 雁:yàn 虾:xiā 蟹:xiè 贝:bèi 龟:guī 羽:yǔ 尾:wěi 皮:pí",
            "植物四季" to "树:shù 林:lín 森:sēn 竹:zhú 叶:yè 根:gēn 枝:zhī 种:zhǒng 苗:miáo 麦:mài 禾:hé 谷:gǔ 松:sōng 柏:bǎi 柳:liǔ 荷:hé 梅:méi 枣:zǎo 梨:lí 杏:xìng 菊:jú 兰:lán 石:shí 沙:shā 河:hé 湖:hú 海:hǎi 江:jiāng 泉:quán 冰:bīng",
            "心情品格" to "爱:ài 好:hǎo 乐:lè 喜:xǐ 怕:pà 气:qì 忙:máng 累:lèi 勇:yǒng 敢:gǎn 乖:guāi 亲:qīn 善:shàn 礼:lǐ 诚:chéng 信:xìn 帮:bāng 助:zhù 同:tóng 合:hé 让:ràng 等:děng 静:jìng 清:qīng 净:jìng 安:ān 全:quán 康:kāng 健:jiàn 梦:mèng",
        )
        val used = coreCharacterLessons.mapTo(mutableSetOf()) { it.character }
        return groups.flatMap { (category, raw) ->
            raw.split(' ').mapNotNull { token ->
                val parts = token.split(':')
                if (parts.size != 2 || !used.add(parts[0])) null
                else CharacterLesson(
                    character = parts[0],
                    pinyin = parts[1],
                    category = category,
                    meaning = "生活中常见的“${parts[0]}”字",
                    words = listOf(parts[0], "找一找${parts[0]}"),
                    picture = listOf("🌟", "🧩", "🌱", "🎈", "📚")[used.size % 5],
                )
            }
        }.take(240)
    }

    private fun story(id: String, title: String, focus: String, vararg pages: String) =
        StoryLesson(id, title, focus, pages.toList())

    private fun rhyme(id: String, title: String, focus: String, vararg lines: String) =
        RhymeLesson(id, title, focus, lines.toList())
}
