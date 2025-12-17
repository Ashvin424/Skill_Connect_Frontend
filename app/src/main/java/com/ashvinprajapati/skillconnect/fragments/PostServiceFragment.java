package com.ashvinprajapati.skillconnect.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.adapters.SelectImageAdapter;
import com.ashvinprajapati.skillconnect.models.ImageUploadResponse;
import com.ashvinprajapati.skillconnect.models.Service;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.ServicesApiService;
import com.ashvinprajapati.skillconnect.utils.RealPathUtil;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.ashvinprajapati.skillconnect.utils.UriToFileUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostServiceFragment extends Fragment {
    private static final int IMAGE_PICK_CODE = 1000;
    private TextInputEditText serviceTitleEditText, serviceDescriptionEditText, categoryTitleEditText;
    private MaterialButton uploadImageBtn;
    private RecyclerView imageRecyclerView;
    private MaterialButton postServiceBtn;
    private SelectImageAdapter imageAdapter;
    private LinearLayout postServiceLayout;
    private LottieAnimationView loadingAnim;
    private List<String> imageUris = new ArrayList<>();
    private List<String> uploadedImageUrls = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostServiceFragment newInstance(String param1, String param2) {
        PostServiceFragment fragment = new PostServiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_service, container, false);
        serviceTitleEditText = view.findViewById(R.id.serviceTitleEditText);
        loadingAnim = view.findViewById(R.id.loadingAnim);
        postServiceLayout = view.findViewById(R.id.postServiceLayout);
        serviceDescriptionEditText = view.findViewById(R.id.serviceDescriptionEditText);
        uploadImageBtn = view.findViewById(R.id.imageUploadBtn);
        postServiceBtn = view.findViewById(R.id.postServiceBtn);
        imageRecyclerView = view.findViewById(R.id.imageRecyclerView);
        categoryTitleEditText = view.findViewById(R.id.categoryTitleEditText);
        imageAdapter = new SelectImageAdapter(imageUris, getContext()); // Pass the context here
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));   // Set the layout manager
        imageRecyclerView.setAdapter(imageAdapter); // Set the adapter


        uploadImageBtn.setOnClickListener(v->{
           selectImage();
        });
        postServiceBtn.setOnClickListener(v -> createService());

        return view;
    }



    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Set the type of files you want to allow
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple selections
        intent.setAction(Intent.ACTION_GET_CONTENT);    // Set the action to GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Images"), IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK){
            if (data.getClipData() != null) { // That means multiple images are selected
                int count = data.getClipData().getItemCount();
                for(int i = 0; i < count; i++){
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();    // Get the URI of the selected image
                    imageUris.add(imageUri.toString());    // Add the URI to the list
                }
            } else if (data.getData() != null){ // That means only one image is selected
                Uri imageUri = data.getData();
                imageUris.add(imageUri.toString());
            }
            imageAdapter.notifyDataSetChanged();
            imageRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    private void createService() {
        String title = serviceTitleEditText.getText().toString();
        String category = categoryTitleEditText.getText().toString();
        String description = serviceDescriptionEditText.getText().toString();
        if (title.isEmpty() || category.isEmpty() || description.isEmpty()){
            Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Service service = new Service();
        service.setTitle(title);
        service.setCategory(category);
        service.setDescription(description);
        //set layout not editable or touchable
        postServiceLayout.setClickable(false);
        postServiceLayout.setFocusable(false);
        postServiceLayout.setAlpha(0.5f);
        loadingAnim.setVisibility(View.VISIBLE);
        ServicesApiService apiService = ApiClient.getClient(requireContext()).create(ServicesApiService.class);
        apiService.createService(service).enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (!isAdded()) return; // Fragment not visible, ignore response
                if (response.isSuccessful() && response.body() != null) {
                    Long serviceId = response.body().getId();
                    TokenManager tokenManager = new TokenManager(requireContext());
                    Log.d("TOKEN_BEFORE_CALL_RESPONSE", "Token in UploadImages: " + tokenManager.getToken());
                    uploadImages(serviceId);

                } else {
                    Toast.makeText(requireContext(), "Failed to create service.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                if (!isAdded()) return; // Fragment not visible, ignore response
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImages(Long serviceId) {
        List<MultipartBody.Part> imageParts = new ArrayList<>();

        for (String uri : imageUris) {
            File file = UriToFileUtil.getFileFromUri(requireContext(), Uri.parse(uri));
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("files", file.getName(), requestBody);
            imageParts.add(part);
        }

        ServicesApiService servicesApiService = ApiClient.getClient(requireContext()).create(ServicesApiService.class);
        TokenManager tokenManager = new TokenManager(requireContext());

        Log.d("TOKEN_BEFORE_UPLOAD", "Token: " + tokenManager.getToken());

        servicesApiService.uploadServiceImages(serviceId, imageParts).enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (!isAdded()) return; // Fragment not visible, ignore response
                if (response.isSuccessful() && response.body() != null) {
                    uploadedImageUrls = response.body().getImageUrls();
                    Toast.makeText(requireContext(), "Service Posted Successfully", Toast.LENGTH_SHORT).show();
                    clearPostServiceFields();
                    loadingAnim.setVisibility(View.GONE);
                    postServiceLayout.setAlpha(1);
                    postServiceLayout.setClickable(true);
                    postServiceLayout.setFocusable(true);

                } else {
                    Toast.makeText(requireContext(), "Failed to upload images.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                if (!isAdded()) return; // Fragment not visible, ignore response
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearPostServiceFields() {
        serviceTitleEditText.setText("");
        serviceDescriptionEditText.setText("");
        categoryTitleEditText.setText("");
        imageUris.clear();
        imageAdapter.notifyDataSetChanged();
        imageRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}