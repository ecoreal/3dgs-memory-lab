package com.cqq._3dobject;

import com.cqq._3dobject.entity.Category;
import com.cqq._3dobject.entity.ModelEntity;
import com.cqq._3dobject.entity.User;
import com.cqq._3dobject.repository.CategoryRepository;
import com.cqq._3dobject.repository.ModelRepository;
import com.cqq._3dobject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        // 迁移旧的明文密码为 BCrypt
        userRepository.findAll().forEach(u -> {
            if (!u.getPasswordHash().startsWith("$2a$")) {
                u.setPasswordHash(passwordEncoder.encode(u.getPasswordHash()));
                userRepository.save(u);
                System.out.println("Migrated password for user: " + u.getEmail());
            }
        });

        List<String> categoryNames = Arrays.asList("赛博艺术", "复古家电", "工业遗迹", "生活百货");
        for (String name : categoryNames) {
            boolean exists = categoryRepository.findAll().stream().anyMatch(c -> c.getName().equals(name));
            if (!exists) {
                categoryRepository.save(new Category(name));
                System.out.println("Initialized category: " + name);
            }
        }

        if (userRepository.count() == 0) {
            User user = new User();
            user.setNickname("TimeTraveler");
            user.setEmail("demo@example.com");
            user.setPasswordHash(passwordEncoder.encode("123456"));
            user.setBio("Collecting memories from the past.");
            user.setAvatarUrl("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80");
            userRepository.save(user);
            System.out.println("Initialized demo user (password: 123456)");
        }

        String targetModelTitle = "老式留声机";
        String targetCategoryName = "赛博艺术";

        Optional<Category> cyberArtCat = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equals(targetCategoryName))
                .findFirst();

        if (cyberArtCat.isPresent()) {
            boolean modelExists = modelRepository.findByIsPublicTrue().stream()
                    .anyMatch(m -> m.getTitle().equals(targetModelTitle));

            if (!modelExists) {
                ModelEntity model = new ModelEntity();
                model.setTitle(targetModelTitle);
                model.setCategoryId(cyberArtCat.get().getId());
                model.setStory("这台留声机见证了1920年代的爵士乐盛世，现在经过赛博改装，可以播放全息影像。");
                model.setProductionYear(1925);
                model.setThumbnailUrl("https://img.js.design/assets/smartFill/img342164da74a8a8.png");

                List<User> users = userRepository.findAll();
                if (!users.isEmpty()) {
                    model.setUid(users.get(0).getUid());
                } else {
                    model.setUid(1L);
                }

                model.setIsPublic(true);
                modelRepository.save(model);
                System.out.println("Initialized model: " + targetModelTitle);
            }
        }
    }
}
