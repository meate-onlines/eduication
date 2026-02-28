package com.eduication.language.config;

import com.eduication.language.entity.Course;
import com.eduication.language.entity.CourseResource;
import com.eduication.language.entity.MarketingArticle;
import com.eduication.language.entity.MembershipPlan;
import com.eduication.language.entity.WordItem;
import com.eduication.language.enums.AccessLevel;
import com.eduication.language.enums.ResourceType;
import com.eduication.language.repository.CourseRepository;
import com.eduication.language.repository.CourseResourceRepository;
import com.eduication.language.repository.MarketingArticleRepository;
import com.eduication.language.repository.MembershipPlanRepository;
import com.eduication.language.repository.WordItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedDemoData(MembershipPlanRepository membershipPlanRepository,
                                          MarketingArticleRepository marketingArticleRepository,
                                          CourseRepository courseRepository,
                                          CourseResourceRepository courseResourceRepository,
                                          WordItemRepository wordItemRepository) {
        return args -> {
            if (membershipPlanRepository.count() == 0) {
                MembershipPlan monthly = new MembershipPlan();
                monthly.setName("月度会员");
                monthly.setPrice(new BigDecimal("39.90"));
                monthly.setDurationDays(30);
                monthly.setDescription("适合轻量学习用户");
                membershipPlanRepository.save(monthly);

                MembershipPlan yearly = new MembershipPlan();
                yearly.setName("年度会员");
                yearly.setPrice(new BigDecimal("299.00"));
                yearly.setDurationDays(365);
                yearly.setDescription("适合长期系统学习用户");
                membershipPlanRepository.save(yearly);
            }

            if (marketingArticleRepository.count() == 0) {
                MarketingArticle article = new MarketingArticle();
                article.setTitle("7天养成外语学习习惯");
                article.setSummary("从零开始，通过微学习方法快速进入状态。");
                article.setContent("通过每日20分钟输入+输出练习，配合课程和单词复习，形成长期学习闭环。");
                article.setPublished(true);
                marketingArticleRepository.save(article);
            }

            if (courseRepository.count() == 0) {
                Course normalCourse = new Course();
                normalCourse.setTitle("英语口语入门");
                normalCourse.setLanguage("英语");
                normalCourse.setLevel("A1");
                normalCourse.setDescription("建立基础发音和日常表达能力。");
                normalCourse.setAccessLevel(AccessLevel.NORMAL);
                normalCourse = courseRepository.save(normalCourse);

                Course vipCourse = new Course();
                vipCourse.setTitle("商务英语实战");
                vipCourse.setLanguage("英语");
                vipCourse.setLevel("B2");
                vipCourse.setDescription("覆盖会议、邮件和谈判场景。");
                vipCourse.setAccessLevel(AccessLevel.VIP);
                vipCourse = courseRepository.save(vipCourse);

                CourseResource r1 = new CourseResource();
                r1.setCourse(normalCourse);
                r1.setTitle("发音规则导学");
                r1.setResourceType(ResourceType.VIDEO);
                r1.setResourceUrl("https://example.com/resource/pronunciation");
                r1.setAccessLevel(AccessLevel.NORMAL);
                r1.setDescription("掌握元音和辅音发音要点");
                courseResourceRepository.save(r1);

                CourseResource r2 = new CourseResource();
                r2.setCourse(vipCourse);
                r2.setTitle("商务会议表达模板");
                r2.setResourceType(ResourceType.ARTICLE);
                r2.setResourceUrl("https://example.com/resource/business-meeting");
                r2.setAccessLevel(AccessLevel.VIP);
                r2.setDescription("高频会议表达句型");
                courseResourceRepository.save(r2);
            }

            if (wordItemRepository.count() == 0) {
                WordItem normalWord = new WordItem();
                normalWord.setLanguage("英语");
                normalWord.setWord("habit");
                normalWord.setPhonetic("/ˈhæbɪt/");
                normalWord.setMeaning("习惯");
                normalWord.setExampleSentence("Reading every day is a good habit.");
                normalWord.setAccessLevel(AccessLevel.NORMAL);
                wordItemRepository.save(normalWord);

                WordItem vipWord = new WordItem();
                vipWord.setLanguage("英语");
                vipWord.setWord("negotiate");
                vipWord.setPhonetic("/nɪˈɡəʊʃieɪt/");
                vipWord.setMeaning("谈判");
                vipWord.setExampleSentence("They need to negotiate a better contract.");
                vipWord.setAccessLevel(AccessLevel.VIP);
                wordItemRepository.save(vipWord);
            }
        };
    }
}
