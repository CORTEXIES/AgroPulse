package com.github.cortex.agro.service;

import com.github.cortex.agro.dto.AgroMessage;
import com.github.cortex.agro.dto.Agronomist;
import com.github.cortex.database.repository.UnclassifiedMessageRepository;
import com.github.cortex.message.buffer.AgroMessageBuffer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AgroMessageProcessingIntegrationTest {

    @Autowired
    private AgroMessageProcessingScheduler scheduler;

    @Autowired
    private AgroMessageBuffer agroMessageBuffer;

    @Autowired
    private UnclassifiedMessageRepository unclassifiedMessageRepository;

    @BeforeAll
    void setUp() {
        agroMessageBuffer.getAllAndClear();
        unclassifiedMessageRepository.deleteAll();
    }

    @Test
    void shouldClassifyMessages() throws InterruptedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        List<String> messages = List.of(
                "Колхоз прогресс\n12.9.44\nуборка пш под отц тр\n425346 день\n3633 от начала\n\nвыравн силос\n324 день\n2523 от начала\n\nвыкаш с св под отц тр\n43633 день\n45636 от начала",
                "Внесение гербицидов по оз.рапсу ПУ\"\"Юг\"\"- 154/154\nОтд 11-79/79\nОтд 16-75/75",
                "ТСК\nВспашка под кукурузу 87 га/ с нарастающим 1307 га ( 95%) Остаток 70 га\nВыравнивание зяби под сою 65 га/ с нарастающим 179 га (3%) Остаток 5918",
                "13.11 Мир\nПахота зяби под кукурузу 32 га день, 505 га от начала, 75%, 167 га остаток.\nПахота зяби под сою 70 га день, 739 га от начала, 65%, 400 га остаток.\nРаботало 5 агрегатов.\nВыравнивание зяби под сахарную свёклу 100 га день, 744 га от начала, 78 %, 208 га остаток.\nРаботал 1 агрегат.",
                "Диск оз пшеницы \nПо Пу 40/8422\nОтд 17 40/1719\n\n2-е диск под сах св\nПо Пу 112/593\nОтд 16 112/370\n\n2-е диск под Вика+трет\nОтд 11 110/110\n\n2-е диск под оз рапс\nОтд 12 66/98\n\nДиск Кук силос\nОтд 17 60/184\n\nЧизел под оз ячмень\n(дракула)\nОтд 16 60/87\n\nПахота под сах св\nОтд 12 15/15\n\nИнсектиц обр подсол\nАвиа\nПо Пу 91/824\nОтд 17 91/91",
                "Уборка свеклы 26.10.день\nОтд1-123/500\nПо ПУ 450/500\nВал 12837360/157848399\nУрожайность 250/270\nПо ПУ 1259680/41630600",
                "Полевые работы АОР, 15.11.2024.\nПахота под кукурузу на зерно:\nДень - 26 га \nОт начала - 3957га (96%)\nОстаток - 140 га \n\nПахота под сою:\nДень - 201 га \nОт начала - 7005 га (82%)\nОстаток- 1572 га \n\nПахота под кукурузу на силос:\nДень - 137 га \nОт начала - 2018 га(65%)\nОстаток - 1068 га \n\nЧизелевание под сою:\nДень -66 га \nОт начала - 907 га\n\nВыравнивание зяби под сахарную свеклу:\nДень - 312 га \nОт начала-8194 га (73%)\nОстаток - 3056 га\n\nВыравнивание зяби под подсолнечник:\nДень - 143 га \nОт начала - 1430 га (34%)\nОстаток - 2780 га \n\n2 след выравнивания зяби под сах.свеклу:\nДень- 181 га \nОт начала- 840 га (7%)\nОстаток - 10410 га",
                "Колхоз Прогресс: гербицидная обработка оз.пшеницы за день 191 га , от начала 376 га (27 %) остаток 1021 га\nПредпосевная культивация под сев кукурузы на силос за день 40 га , от начала 40 га (8 %), остаток 461 га\nСеа кукурузы на силос за день 20 га от начала 20 га (4%), остаток 481 га\nОсадки 1 мм",
                "28.10\nВнесение мин удобрений под оз пшеницу 2025 г ПУ Юг 344/8346\nОтд 12- 174/2453\nОтд 16-170/1474",
                "Пахота зяби под сою\nПо Пу 6/1500\nОтд 17 6/239\n\nДисков сах св\nПо Пу 192/1986\nОтд 12 82/567\nОтд 16 110/474\n\n2-е диск сах св под пш\nПо Пу 180/1681\nОтд 16 179/474\nОтд 17 1/670",
                "26.10\nВнесение мин удобрений под оз пшеницу 2025 г ПУ Юг 81/8002\nОтд 16-81/1304",
                "Пахота зяби под сою\nПо Пу 8/1494\nОтд 17 8/233\n\nДисков сах св\nПо Пу 139/1794\nОтд 16 95/364\nОтд 17 44/728\n\n2-е диск сах св под пш\nПо Пу 155/1656\nОтд 16 155/295",
                "Пахота зяби под сою\nПо Пу 11/1486\nОтд 17 11/225\n\nДисков сах св\nПо Пу 118/1655\nОтд 16 118/269\n\n2-е диск сах св под пш\nПо Пу 22/1501\nОтд 16 17/140\nОтд 17 5/669",
                "Пахота зяби под сою\nПо Пу 11/1475\nОтд 17 11/214\n\nДискован сах св\nПо Пу 146/1537\nОтд 16 126/151\nОтд 17 20/684\n\n2-е диск сах св под пш\nПо Пу 191/1479\nОтд 16 123/123\nОтд 17 68/664\n\nДиск аренда НПО кук/з\nОтд 17 15/15\n\nВыравн зяби под сах св\nПо Пу 13/1408\nОтд 11 13/504\n\nВыравн зяби под подс\nОтд 11 28/117",
                "31.03.25\nСП Коломейцево \n\nВнесение почвенного гербицида по подсолнечнику \n\nДень 48га\nОт начала 130га(63%)",
                "29.03.25г.\nСП Коломейцево\n\nпредпосевная культивация  \n  -под подсолнечник\n    день+ночь 97га\n    от начала 157га(77%)\n\nСев сахарной свеклы: \nДень 19га\nОт начала 295га(100%)\n\nсев подсолнечника \n  день+ночь 75га\n  от начала 100га(50%)\n\nСплошная культивация под сою:\n  день 51га \n  от начала 251га (100%)",
                "31.03.25\nСП Коломейцево \n\nВнесение почвенного гербицида по подсолнечнику \n\nДень 48га\nОт начала 130га(63%)",
                "Полевые работы АОР, 15.11.2024.\nПахота под кукурузу на зерно:\nДень - 26 га \nОт начала - 3957га (96%)\nОстаток - 140 га \n\nПахота под сою:\nДень - 201 га \nОт начала - 7005 га (82%)\nОстаток- 1572 га \n\nПахота под кукурузу на силос:\nДень - 137 га \nОт начала - 2018 га(65%)\nОстаток - 1068 га \n\nЧизелевание под сою:\nДень -66 га \nОт начала - 907 га\n\nВыравнивание зяби под сахарную свеклу:\nДень - 312 га \nОт начала-8194 га (73%)\nОстаток - 3056 га\n\nВыравнивание зяби под подсолнечник:\nДень - 143 га \nОт начала - 1430 га (34%)\nОстаток - 2780 га \n\n2 след выравнивания зяби под сах.свеклу:\nДень- 181 га \nОт начала- 840 га (7%)\nОстаток - 10410 га",
                "Уборка свеклы 24.10.день\nОтд17-28/150\nПо ПУ 110/780\nВал 12837360/157848399\nУрожайность 250/270\nПо ПУ 1259680/41630600"
        );

        for (String text : messages) {
            AgroMessage msg = new AgroMessage(
                    new Agronomist("name", "10"),
                    Optional.of(text),
                    Optional.empty()
            );
            agroMessageBuffer.add(msg);
        }

        Method method = AgroMessageProcessingScheduler.class.getDeclaredMethod("processPendingMessages");
        method.setAccessible(true);
        method.invoke(scheduler);

        Thread.sleep(60000);
        long count = unclassifiedMessageRepository.count();
        System.out.println("Unclassified messages count: " + count);

        assertEquals(0, count);
    }
}
