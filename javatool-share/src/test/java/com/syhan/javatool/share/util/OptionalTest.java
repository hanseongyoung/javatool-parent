package com.syhan.javatool.share.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class OptionalTest {
    //
    @Test
    public void whenCreatedEmptyOptional_thenCorrect() {
        Optional<String> empty = Optional.empty();
        Assert.assertFalse(empty.isPresent());
    }

    @Test
    public void givenNonNull_whenCreatesOptional_thenCorrect() {
        String name = "syhan";
        Optional<String> opt = Optional.of(name);
        Assert.assertEquals("Optional[syhan]", opt.toString());
    }

    @Test(expected = NullPointerException.class)
    public void givenNull_whenThrowsErrorOnCreate_thenCorrect() {
        String name = null;
        Optional<String> opt = Optional.of(name);
    }

    @Test
    public void givenNonNull_whenCreatesNullable_thenCorrect() {
        String name = "syhan";
        Optional<String> opt = Optional.ofNullable(name);
        Assert.assertEquals("Optional[syhan]", opt.toString());
    }

    @Test
    public void giveNull_whenCreatesNullable_thenCorrect() {
        String name = null;
        Optional<String> opt = Optional.ofNullable(name);
        Assert.assertEquals("Optional.empty", opt.toString());
    }

    @Test
    public void givenOptional_whenIsPresentWorks_thenCorrenct() {
        Optional<String> opt = Optional.of("syhan");
        Assert.assertTrue(opt.isPresent());

        opt = Optional.ofNullable(null);
        Assert.assertFalse(opt.isPresent());
    }

    ////////////////////////////////////////

    @Test
    public void givenOptional_whenIfPresentWorks_thenCorrect() {
        Optional<String> opt = Optional.of("syhan");
        opt.ifPresent(name -> System.out.println(name.length()));
    }

    @Test
    public void whenOrElseWorks_thenCorrect() {
        String nullName = null;
        String name = Optional.ofNullable(nullName).orElse("john");
        Assert.assertEquals("john", name);
    }

    @Test
    public void whenOrElseGetWorks_thenCorrect() {
        String nullName = null;
        String name = Optional.ofNullable(nullName).orElseGet(() -> "john");
        Assert.assertEquals("john", name);
    }

    public String getMyDefalut() {
        System.out.println("Getting Default Value");
        return "Default Value";
    }

    @Test
    public void whenOrElseGetAndOrElseOverlap_thenCorrect() {
        String text = null;

        System.out.println("Using orElseGet:");
        String defaultText = Optional.ofNullable(text).orElseGet(this::getMyDefalut);
        Assert.assertEquals("Default Value", defaultText);

        System.out.println("Using orElse:");
        defaultText = Optional.ofNullable(text).orElse(getMyDefalut());
        Assert.assertEquals("Default Value", defaultText);
    }

    @Test
    public void whenOrElseGetAndOrElseDiffer_thenCorrect() {
        String text = "Text present";

        System.out.println("Using orElseGet:");
        String defaultText = Optional.ofNullable(text).orElseGet(this::getMyDefalut);
        Assert.assertEquals("Text present", defaultText);

        System.out.println("Using orElse:");
        defaultText = Optional.ofNullable(text).orElse(getMyDefalut());
        Assert.assertEquals("Text present", defaultText);
    }

    ////////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void whenOrElseThrowWorks_thenCorrect() {
        String nullName = null;
        String name = Optional.ofNullable(nullName).orElseThrow(IllegalArgumentException::new);
    }

    @Test
    public void giveOptional_whenGetsValue_thenCorrect() {
        Optional<String> opt = Optional.of("syhan");
        String name = opt.get();
        Assert.assertEquals("syhan", name);
    }

    @Test(expected = NoSuchElementException.class)
    public void giveOptionalWithNull_whenGetThrowsException_thenCorrect() {
        Optional<String> opt = Optional.ofNullable(null);
        String name = opt.get();
    }

    ////////////////////////////////////////

    @Test
    public void whenOptionalFilterWorks_thenCorrect() {
        Integer year = 2016;
        Optional<Integer> yearOptional = Optional.of(year);
        boolean is2016 = yearOptional.filter(y -> y == 2016).isPresent();
        Assert.assertTrue(is2016);
        boolean is2017 = yearOptional.filter(y -> y == 2017).isPresent();
        Assert.assertFalse(is2017);
    }

    ////////////////////////////////////////
    public class Modem {
        private Double price;

        public Modem(Double price) {
            this.price = price;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }

    public boolean priceIsInRange(Modem modem) {
        boolean isInRange = false;

        if (modem != null && modem.getPrice() != null
                && (modem.getPrice() >= 10 && modem.getPrice() <= 15)) {
            isInRange = true;
        }
        return isInRange;
    }

    @Test
    public void whenFiltersWithoutOptional_thenCorrect() {
        // range is 10 ~ 15
        Assert.assertTrue(priceIsInRange(new Modem(10.0)));
        Assert.assertFalse(priceIsInRange(new Modem(9.9)));
        Assert.assertFalse(priceIsInRange(new Modem(null)));
        Assert.assertFalse(priceIsInRange(new Modem(15.5)));
        Assert.assertFalse(priceIsInRange(null));
    }

    public boolean priceIsInRange2(Modem modem) {
        return Optional.ofNullable(modem)
                .map(Modem::getPrice)
                .filter(p -> p >= 10)
                .filter(p -> p <= 15)
                .isPresent();
    }

    @Test
    public void whenFiltersWithOptional_thenCorrect() {
        // range is 10 ~ 15
        Assert.assertTrue(priceIsInRange2(new Modem(10.0)));
        Assert.assertFalse(priceIsInRange2(new Modem(9.9)));
        Assert.assertFalse(priceIsInRange2(new Modem(null)));
        Assert.assertFalse(priceIsInRange2(new Modem(15.5)));
        Assert.assertFalse(priceIsInRange2(null));
    }

    ////////////////////////////////////////

    @Test
    public void givenOptional_whenMapWorks_thenCorrect() {
        List<String> companyNames = Arrays.asList("paypal", "oracle", "", "microsoft", "", "apple");
        Optional<List<String>> listOptional = Optional.of(companyNames);

        int size = listOptional
                .map(List::size)
                .orElse(0);
        Assert.assertEquals(6, size);
    }

    @Test
    public void givenOptional_whenMapWorks_thenCorrect2() {
        String name = "syhan";
        Optional<String> nameOptional = Optional.of(name);

        int len = nameOptional
                .map(String::length)
                .orElse(0);
        Assert.assertEquals(5, len);
    }

    @Test
    public void givenOptional_whenMapWorksWithFilter_thenCorrect() {
        String password = " password ";
        Optional<String> passOpt = Optional.of(password);
        boolean correctPassword = passOpt
                .filter(pass -> pass.equals("password"))
                .isPresent();
        Assert.assertFalse(correctPassword);

        correctPassword = passOpt
                .map(String::trim)
                .filter(pass -> pass.equals("password"))
                .isPresent();
        Assert.assertTrue(correctPassword);
    }

    ////////////////////////////////////////
    // map : transforms unwrapped value
    // flatmap : transform wrapped value

    public class Person {
        private String name;
        private int age;
        private String password;
        private String email;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public Optional<String> getName() {
            return Optional.ofNullable(name);
        }

        public Optional<Integer> getAge() {
            return Optional.ofNullable(age);
        }

        public Optional<String> getPassword() {
            return Optional.ofNullable(password);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    @Test
    public void givenOptional_whenFlatMapWorks_thenCorrect() {
        Person person = new Person("john", 26);
        Optional<Person> personOptional = Optional.of(person);

        //
        Optional<Optional<String>> nameOptionalWrapper = personOptional.map(Person::getName);
        Optional<String> nameOptional = nameOptionalWrapper.orElseThrow(IllegalArgumentException::new);
        String name = nameOptional.orElse("");
        Assert.assertEquals("john", name);

        //
        String name2 = personOptional.flatMap(Person::getName).orElse("");
        Assert.assertEquals("john", name2);
    }

    ////////////////////////////////////////
    @Test
    public void testOrElse() {
        Person person = new Person("john", 26);
        person.setEmail("john@test.com");

        Optional<Person> personOptional = Optional.of(person);
        String email = personOptional
                .map(Person::getEmail)
                .orElse(null);
        System.out.println(email);
    }

    @Test
    public void testOrElse2() {
        String nullName = null;
        String name = Optional.ofNullable(nullName).orElse(null);
        System.out.println("name:"+name);
        Assert.assertNull(name);
    }
}
