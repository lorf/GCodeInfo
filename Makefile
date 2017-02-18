SRCS = $(patsubst $(SRCDIR)/%,%,$(shell find $(SRCDIR) -name \*.java))
MAIN = de.dietzm.GCodeUtil
CLASSPATH = /usr/share/java/RXTXcomm.jar
JARNAME = GCodeInfo
JARDIR = bin
ZIPDIR = zip
SRCDIR = GCodeInfo/src
BINDIR = GCodeInfo/bin
ZIP_FILES = README.md bin VERSION

JFLAGS = -g
JC = javac
JVM = java 
JAR = jar

.SUFFIXES: .java .class .jar

CLASSES = $(SRCS:.java=.class)
GIT_REVISION := $(shell git log -1 --format=%h 2>/dev/null)
JARPATH = $(JARDIR)/$(JARNAME).jar
ZIPNAME = $(JARNAME)-$(shell cat VERSION)
ZIPPATH = $(ZIPDIR)/$(ZIPNAME).zip

all: $(JARPATH)

$(JARPATH): $(addprefix $(BINDIR)/,$(CLASSES))
	$(JAR) cvfe $@ $(MAIN) -C $(BINDIR) .

$(BINDIR)/%.class: $(SRCDIR)/%.java
	-mkdir -p $(dir $@)
	$(JC) $(JFLAGS) -s $(SRCDIR) -d $(BINDIR) -cp $(BINDIR):$(CLASSPATH) -sourcepath $(SRCDIR) $<

run: $(JARPATH)
	$(JVM) -jar $<

zip: $(ZIPPATH)

$(ZIPPATH): $(ZIP_FILES)
	$(RM) -r $(ZIPDIR)/$(ZIPNAME)
	mkdir -p $(ZIPDIR)/$(ZIPNAME)
	for p in $(ZIP_FILES); do \
		mkdir -p $(ZIPDIR)/$(ZIPNAME)/`dirname $$p`; \
		cp -Rp $$p $(ZIPDIR)/$(ZIPNAME)/`dirname $$p`; \
	done
	cd $(ZIPDIR) && zip -9r $(ZIPNAME).zip $(ZIPNAME)
	$(RM) -r $(ZIPDIR)/$(ZIPNAME)

clean:
	$(RM) -r $(BINDIR) $(JARPATH) $(ZIPPATH)
