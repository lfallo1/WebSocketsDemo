package com.lancefallon.controllers;

//@Controller
//public class ErrorPageController implements ErrorController {
//
//	@Autowired
//	private ErrorService errorService;
//
//	@RequestMapping("/error")
//	public String redirectNonExistentUrlsToHome(HttpServletResponse response, Model model) throws IOException {
//		model.addAttribute("error", this.errorService.getErrorByStatus(response.getStatus()));
//		return "error";
//	}
//
//	@Override
//	public String getErrorPath() {
//		return "/error";
//	}
//}