FROM maven

ENV REPO_URL="https://github.com/e-ucm/lostinspace" \
    REPO_TAG="master" \
    USER_NAME="user" \
    WORK_DIR="/app"

# setup user, group and workdir
RUN groupadd -r ${USER_NAME} \
    && useradd -r -d ${WORK_DIR} -g ${USER_NAME} ${USER_NAME} \
    && mkdir ${WORK_DIR} \
    && chown ${USER_NAME}:${USER_NAME} ${WORK_DIR}
USER ${USER_NAME}
ENV HOME=${WORK_DIR}
WORKDIR ${WORK_DIR}

# retrieve sources
RUN git clone -b "$REPO_TAG" --single-branch "$REPO_URL" .

# install a pesky dependency into the m2 cache
RUN mkdir xt \
  && cd xt \
  && git clone --single-branch https://github.com/e-ucm/xmltools . \
  && mvn install \
  && cd ${WORK_DIR}

# get (others) dependencies sorted out, and compile everything
RUN mvn install -P html,-default

# expose & run
EXPOSE 9090
CMD [ "mvn", "-Djetty.port=9999", "install", "-P", "html,jetty" ]

# access via ip:9090/setup
