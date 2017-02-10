SRCS = $(patsubst $(SRCDIR)/%,%,$(shell find $(SRCDIR) -name \*.java))
MAIN = de.dietzm.GCodeUtil
CLASSPATH = /usr/share/java/RXTXcomm.jar
JARNAME = GCodeInfo.jar
SRCDIR = GCodeInfo/src
BINDIR = GCodeInfo/bin
ZIP_FILES = $(JARNAME) README.md

JFLAGS = -g
JC = javac
JVM = java 
JAR = jar

.SUFFIXES: .java .class .jar

CLASSES = $(SRCS:.java=.class)
GIT_REVISION := $(shell git log -1 --format=%h 2>/dev/null)
ZIP_NAME = $(basename $(JARNAME))-$(shell cat VERSION)

all: $(JARNAME)

$(JARNAME): $(addprefix $(BINDIR)/,$(CLASSES))
	$(JAR) cvfe $@ $(MAIN) -C $(BINDIR) .

$(BINDIR)/%.class: $(SRCDIR)/%.java
	-mkdir -p $(dir $@)
	$(JC) $(JFLAGS) -s $(SRCDIR) -d $(BINDIR) -cp $(BINDIR):$(CLASSPATH) -sourcepath $(SRCDIR) $<

run: $(JARNAME)
	$(JVM) -jar $<

zip: $(ZIP_FILES)
	rm -rf $(ZIP_NAME).zip $(ZIP_NAME)
	mkdir -p $(ZIP_NAME)
	for p in $(ZIP_FILES); do \
		mkdir -p $(ZIP_NAME)/`dirname $$p`; \
		cp -Rp $$p $(ZIP_NAME)/`dirname $$p`; \
	done
	zip -9r $(ZIP_NAME).zip $(ZIP_NAME)
	rm -rf $(ZIP_NAME)

clean:
	$(RM) -r $(BINDIR) $(JARNAME) $(ZIP_NAME).zip
