const logout = () => {
  localStorage.removeItem("user");
}

const setCurrentUser = (token) => {
  localStorage.setItem("user", token);
}

const getCurrentUser = () => {
  return JSON.parse(localStorage.getItem("user"));
}

const AuthService = {
  logout,
  setCurrentUser,
  getCurrentUser,
};

export default AuthService;